package ru.alexeyp.printerservice

import android.content.Context
import android.net.nsd.NsdManager
import android.print.PrintManager
import android.support.v7.app.AppCompatActivity
import ru.alexeyp.printerservice.connectors.BytesPrinterConnector
import ru.alexeyp.printerservice.connectors.FilePrinterConnector
import ru.alexeyp.printerservice.connectors.PaperSize
import ru.alexeyp.printerservice.connectors.StreamPrinterConnector
import ru.alexeyp.printerservice.model.PrinterInfo
import ru.alexeyp.printerservice.print_adapters.ByteArrayPrintAdapter
import ru.alexeyp.printerservice.print_adapters.FilePrintAdapter
import ru.alexeyp.printerservice.print_adapters.PrintAdapter
import ru.alexeyp.printerservice.print_adapters.StreamPrintAdapter
import io.reactivex.Single
import java.io.File
import java.io.InputStream
import java.util.concurrent.TimeUnit

interface IPrintService {

    /**
     * Forms document for preview in standard Android Print Tools, using StreamPrintAdapter
     * @param data data from your file
     * @param name it`s name of document (default = "Document")
     * @see StreamPrintAdapter
     * @see FilePrintAdapter
     * @see ByteArrayPrintAdapter
     */
    fun showDocumentPreview(data: InputStream, name: String = "Document")

    fun showDocumentPreview(data: File, name: String = "Document")

    fun showDocumentPreview(data: ByteArray, name: String = "Document")

    /**
     * Find all printers, available to current network. Timeout for request 3.5 seconds
     * @return Single that provides list of all found printers
     */
    fun findPrinters(): Single<List<PrinterInfo>>

    /**
     * Send data to printer for printing using IP address and port
     * @param ip ip address of printer
     * @param port port of printer
     * @param data data for printing
     * @param filename name of printing file for {@PJL JOB NAME} command
     * @param paperSize size of paper for {@PJL SET PAPER} command
     * @param copies copies of printing document. {@PJL SET COPIES} command
     * @return Single that inform about success printing or error
     * @see FilePrinterConnector
     * @see StreamPrinterConnector
     * @see BytesPrinterConnector
     */
    fun print(ip: String, port: Int, data: File, filename: String = "Document", paperSize: PaperSize = PaperSize.A4, copies: Int = 1): Single<String>

    fun print(ip: String, port: Int, data: InputStream, filename: String = "Document", paperSize: PaperSize = PaperSize.A4, copies: Int = 1): Single<String>

    fun print(ip: String, port: Int, data: ByteArray, filename: String = "Document", paperSize: PaperSize = PaperSize.A4, copies: Int = 1): Single<String>
}

class PrintService(private val context: Context) : IPrintService {

    override fun showDocumentPreview(data: InputStream, name: String) {
        prepareDocument(name, StreamPrintAdapter(data))
    }

    override fun showDocumentPreview(data: File, name: String) {
        prepareDocument(name, FilePrintAdapter(data))
    }

    override fun showDocumentPreview(data: ByteArray, name: String) {
        prepareDocument(name, ByteArrayPrintAdapter(data))
    }

    override fun findPrinters(): Single<List<PrinterInfo>> {
        val nsdManager = context.getSystemService(Context.NSD_SERVICE) as NsdManager
        return PrinterSearcher(nsdManager)
                .find()
                .timeout(4000, TimeUnit.MILLISECONDS)
                .toList()
    }

    override fun print(ip: String, port: Int, data: File, filename: String, paperSize: PaperSize, copies: Int): Single<String> {
        return FilePrinterConnector(data).print(ip, port, filename, paperSize, copies).timeout(4000, TimeUnit.MILLISECONDS)
    }

    override fun print(ip: String, port: Int, data: InputStream, filename: String, paperSize: PaperSize, copies: Int): Single<String> {
        return StreamPrinterConnector(data).print(ip, port, filename, paperSize, copies)
    }

    override fun print(ip: String, port: Int, data: ByteArray, filename: String, paperSize: PaperSize, copies: Int): Single<String> {
        return BytesPrinterConnector(data).print(ip, port, filename, paperSize, copies)
    }

    private fun prepareDocument(name: String, adapter: PrintAdapter) {
        val printManager = context.getSystemService(AppCompatActivity.PRINT_SERVICE) as PrintManager
        printManager.print(name, adapter, null)
    }
}