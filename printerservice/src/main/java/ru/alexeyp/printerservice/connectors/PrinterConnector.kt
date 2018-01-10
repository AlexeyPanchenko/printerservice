package ru.alexeyp.printerservice.connectors

import io.reactivex.Single
import ru.alexeyp.printerservice.PrintFailedException
import java.io.*
import java.net.ConnectException
import java.net.Socket
import java.net.UnknownHostException

internal abstract class PrinterConnector {

    abstract fun writeData(output: DataOutputStream?)

    fun print(ip: String, port: Int, filename: String = "Document", paperSize: PaperSize = PaperSize.A4, copies: Int = 1): Single<String> {
        return Single.create<String> { emitter ->
            try {
                Socket(ip, port).use { socket ->
                    DataOutputStream(socket.getOutputStream()).use { output ->
                        fillPJL(output, filename, paperSize, copies)
                        emitter.onSuccess("Success printing")
                    }
                }
            } catch (e: UnknownHostException) {
                emitter.onError(PrintFailedException("Unknown host: ip $ip, (port $port)"))
            } catch (e: ConnectException) {
                emitter.onError(PrintFailedException("Failed to connect: ip $ip, (port $port)"))
            } catch (e: IOException) {
                emitter.onError(PrintFailedException("Error during write data"))
            }
        }
    }

    private fun fillPJL(output: DataOutputStream?, filename: String, paperSize: PaperSize, copies: Int) {
        val ESC = 0x1b.toChar()
        val UEL = ESC + "%-12345X"
        val ESC_SEQ = ESC + "%-12345\r\n"
        output?.writeBytes(UEL)
        output?.writeBytes("@PJL \r\n")
        output?.writeBytes("@PJL JOB NAME = '$filename' \r\n")
        output?.writeBytes("@PJL SET PAPER=" + paperSize.name)
        output?.writeBytes("@PJL SET COPIES=" + copies)
        output?.writeBytes("@PJL ENTER LANGUAGE = PDF\r\n")
        writeData(output)
        output?.writeBytes(ESC_SEQ)
        output?.writeBytes("@PJL \r\n")
        output?.writeBytes("@PJL RESET \r\n")
        output?.writeBytes("@PJL EOJ NAME = '\$filename'")
        output?.writeBytes(UEL)
    }
}

enum class PaperSize {
    A4, A5
}
