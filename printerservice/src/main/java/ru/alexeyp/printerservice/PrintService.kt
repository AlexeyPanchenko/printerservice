package ru.alexeyp.printerservice

import android.content.Context
import android.net.nsd.NsdManager
import android.print.PrintManager
import android.support.v7.app.AppCompatActivity
import ru.alexeyp.printerservice.model.PrinterInfo
import ru.alexeyp.printerservice.print_adapters.ByteArrayPrintAdapter
import ru.alexeyp.printerservice.print_adapters.FilePrintAdapter
import ru.alexeyp.printerservice.print_adapters.PrintAdapter
import ru.alexeyp.printerservice.print_adapters.StreamPrintAdapter
import io.reactivex.Single
import ru.alexeyp.printerservice.connectors.*
import java.io.File
import java.io.InputStream
import java.util.concurrent.TimeUnit

private val TIMEOUT = 4000L

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
     * @param timeout timeout for discovery printers in milliseconds
     * @return Single that provides list of all found printers
     */
    fun findPrinters(timeout: Long = TIMEOUT): Single<List<PrinterInfo>>

    /**
     * Send data to printer for printing using IP address and port
     * @param ip ip address of printer
     * @param port port of printer
     * @param data data for printing
     * @param filename name of printing file for {@PJL JOB NAME} command
     * @param paperSize size of paper for {@PJL SET PAPER} command
     * @param copies copies of printing document. {@PJL SET COPIES} command
     * @param timeout timeout for executing operation in milliseconds
     * @return Single that inform about success printing or error
     * @see FilePrinterConnector
     * @see StreamPrinterConnector
     * @see BytesPrinterConnector
     */
    fun print(ip: String, port: Int, data: File, filename: String = "Document", paperSize: PaperSize = PaperSize.A4,
              copies: Int = 1, timeout: Long = TIMEOUT): Single<String>

    fun print(ip: String, port: Int, data: InputStream, filename: String = "Document", paperSize: PaperSize = PaperSize.A4,
              copies: Int = 1, timeout: Long = TIMEOUT): Single<String>

    fun print(ip: String, port: Int, data: ByteArray, filename: String = "Document", paperSize: PaperSize = PaperSize.A4,
              copies: Int = 1, timeout: Long = TIMEOUT): Single<String>
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

    override fun findPrinters(timeout: Long): Single<List<PrinterInfo>> {
        val nsdManager = context.getSystemService(Context.NSD_SERVICE) as NsdManager
        return PrinterSearcher(nsdManager)
                .find(timeout)
                .distinct()
                .timeout(timeout + 500, TimeUnit.MILLISECONDS)
                .toList()
    }

    override fun print(ip: String, port: Int, data: File, filename: String, paperSize: PaperSize, copies: Int, timeout: Long): Single<String> {
        return executePrint(FilePrinterConnector(data), ip, port, filename, paperSize, copies, timeout)
    }

    override fun print(ip: String, port: Int, data: InputStream, filename: String, paperSize: PaperSize, copies: Int, timeout: Long): Single<String> {
        return executePrint(StreamPrinterConnector(data), ip, port, filename, paperSize, copies, timeout)
    }

    override fun print(ip: String, port: Int, data: ByteArray, filename: String, paperSize: PaperSize, copies: Int, timeout: Long): Single<String> {
        return executePrint(BytesPrinterConnector(data), ip, port, filename, paperSize, copies, timeout)
    }

    private fun prepareDocument(name: String, adapter: PrintAdapter) {
        val printManager = context.getSystemService(AppCompatActivity.PRINT_SERVICE) as PrintManager
        printManager.print(name, adapter, null)
    }

    private fun executePrint(connector: PrinterConnector, ip: String, port: Int, filename: String, paperSize: PaperSize, copies: Int, timeout: Long): Single<String> {
        return connector.print(ip, port, filename, paperSize, copies)
                .timeout(timeout, TimeUnit.MILLISECONDS)
    }
}