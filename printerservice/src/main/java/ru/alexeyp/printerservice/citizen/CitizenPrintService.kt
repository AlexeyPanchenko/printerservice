package ru.alexeyp.printerservice.citizen

import android.content.Context
import android.graphics.Bitmap
import com.citizen.sdk.CitizenPrinterInfo
import com.citizen.sdk.ESCPOSConst.*
import com.citizen.sdk.ESCPOSPrinter
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import ru.alexeyp.printerservice.DiscoveryFailedException
import ru.alexeyp.printerservice.PrintFailedException
import ru.alexeyp.printerservice.model.PrinterInfo

/**
 * Helper for work with CITIZEN printers CT-D150,CT-E351,
 * CT-S251/310II/601/651/801/851/601II/651II/801II/851II series, using CSJPOSLib_Android
 */
interface ICitizenPrintService {
    /**
     * ~Async (Schedulers.io())~ Find CITIZEN printers, using specific type of connection (Wi-Fi, Bluetooth)
     * @param context for calling printer.setContext(context)
     * @param wifiTimeout time of searching printers for wi-fi searching (1-30s, default = 5)
     * @return Single with list of PrinterInfo
     * @see PrinterInfo
     */
    fun findPrinters(context: Context, wifiTimeout: Int = 5): Single<List<PrinterInfo>>
}

class CitizenPrintService(private val type: PrinterType) : ICitizenPrintService {

    private val printer = ESCPOSPrinter()
    private val connectionType: Int = when(type) {
        PrinterType.WI_FI -> CMP_PORT_WiFi
        PrinterType.BLUETOOTH -> CMP_PORT_Bluetooth
    }

    override fun findPrinters(context: Context, wifiTimeout: Int) = Single.create<List<PrinterInfo>> {
        try {
            printer.setContext(context)
            val searchTime = if (connectionType == CMP_PORT_WiFi) wifiTimeout else 0
            val result = printer.searchCitizenPrinter(connectionType, searchTime, intArrayOf(1))
            val printers = result.map { PrinterInfo(it.name(), it.ipAddress, 9100) }
            it.onSuccess(printers)
        } catch (e: Exception) {
            e.printStackTrace()
            it.onError(DiscoveryFailedException("Discovery CITIZEN printer by ${type.name} was failed"))
        }
    }.subscribeOn(Schedulers.io())

    inner class Printer(private val address: String) {

        /**
         * ~Async (Schedulers.io())~ send text to printer for printing with following cutting of Paper
         * Parameters fo text style can be combined
         * @param text for printing
         * @param alignment for customize printing, default CitizenConfig.Alignment.CENTER
         * @param attributes for customize printing, default CitizenConfig.Font.DEFAULT
         * @param textSize for customize printing, default CitizenConfig.Width.WIDTH1 or CitizenConfig.Height.HEIGHT1
         * @return Completable
         * @see CitizenConfig.Alignment
         * @see CitizenConfig.Font
         * @see CitizenConfig.Width
         * @see CitizenConfig.Height
         */
        fun printText(text: String, alignment: Int = CitizenConfig.Alignment.CENTER, attributes: Int = CitizenConfig.Font.DEFAULT,
                      textSize: Int = CitizenConfig.Width.WIDTH1 or CitizenConfig.Height.HEIGHT1) = Completable.create {
            executeTransaction {
                printer.printText(text, alignment, attributes, textSize)
            }
            it.onComplete()
        }.subscribeOn(Schedulers.io())

        /**
         * ~Async (Schedulers.io())~ send Bitmap to printer for printing with following cutting of Paper
         * @param bitmap for printing
         * @param alignment for customize printing, default CitizenConfig.Alignment.CENTER
         * @return Completable
         * @see CitizenConfig.Alignment
         */
        fun printBitmap(bitmap: Bitmap, alignment: Int = CitizenConfig.Alignment.CENTER) = Completable.create {
            executeTransaction {
                printer.printBitmap(bitmap, alignment)
            }
            it.onComplete()
        }.subscribeOn(Schedulers.io())

        private fun executeTransaction(task: () -> Int) {
            checkStatus(connect())
            printer.setEncoding("UTF-8")
            printer.transactionPrint(CMP_TP_TRANSACTION)
            checkStatus(task())
            printer.cutPaper(CMP_CUT_PARTIAL_PREFEED)
            printer.transactionPrint(CMP_TP_NORMAL)
            disconnect()
        }

        private fun connect() = printer.connect(connectionType, address)

        private fun disconnect() = printer.disconnect()

    }

    /**
     * Throws exception if status is not Success
     */
    private fun checkStatus(result: Int) {
        when(result) {
            CMP_SUCCESS -> return
            CMP_E_DISCONNECT -> throw PrintFailedException("The printer is not connected")
            CMP_E_NOTCONNECT -> throw PrintFailedException("Failed connection to the printer")
            CMP_E_CONNECT_NOTFOUND -> throw PrintFailedException("Failed to check the support model after connecting to the device")
            CMP_E_CONNECT_OFFLINE -> throw PrintFailedException("Failed to check the printer status after connecting to the device")
            CMP_E_NOCONTEXT -> throw PrintFailedException("The context is not specified")
            CMP_E_BT_DISABLE -> throw PrintFailedException("The setting of the Bluetooth device is invalid")
            CMP_E_BT_NODEVICE -> throw PrintFailedException("BlueTooth printer is not found")
            CMP_E_ILLEGAL -> throw PrintFailedException("Unsupported operation with the Device, or an invalid parameter value was used")
            CMP_E_OFFLINE -> throw PrintFailedException("The printer is offline")
            CMP_E_NOEXIST -> throw PrintFailedException("The file name does not exist")
            CMP_E_FAILURE -> throw PrintFailedException("The Service cannot perform the requested procedure")
            CMP_E_TIMEOUT -> throw PrintFailedException("The Service timed out waiting for a response from the printer")
            CMP_E_NO_LIST -> throw PrintFailedException("The printer cannot be found in the printer search")
            CMP_EPTR_COVER_OPEN -> throw PrintFailedException("The cover of the printer opens")
            CMP_EPTR_REC_EMPTY -> throw PrintFailedException("The printer is out of paper")
            CMP_EPTR_BADFORMAT -> throw PrintFailedException("The specified file is in an unsupported format")
            CMP_EPTR_TOOBIG -> throw PrintFailedException("The specified bitmap is either too big")
        }
    }

    /**
     * Set correct name for found printer
     */
    private fun CitizenPrinterInfo.name() = when(type) {
            PrinterType.WI_FI -> if (this.deviceName.isEmpty()) this.ipAddress else this.deviceName
            PrinterType.BLUETOOTH -> if (this.deviceName.isEmpty()) this.bdAddress else this.deviceName
        }

}

