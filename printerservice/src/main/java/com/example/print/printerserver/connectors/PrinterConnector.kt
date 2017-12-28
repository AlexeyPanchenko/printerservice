package com.example.print.printerserver.connectors

import io.reactivex.Single
import java.io.*
import java.net.Socket

internal abstract class PrinterConnector {

    abstract fun writeData(output: DataOutputStream?)

    fun print(ip: String, port: Int, filename: String = "Document", paperSize: Paper = Paper.A4, copies: Int = 1): Single<String> {
        /*return Single.create<String> { emitter ->
            try {
                Socket(ip, port).use { socket ->
                    DataOutputStream(socket.getOutputStream()).use { output ->
                        fillPJL(output, filename, paperSize, copies)
                        emitter.onSuccess("Success printing")
                    }
                }
            } catch (e: Exception) {
                emitter.onError(e)
            }
        }*/
        return Single.create<String> {
            var socket: Socket? = null
            var output: DataOutputStream? = null
            try {
                socket = Socket(ip, port)
                output = DataOutputStream(socket.getOutputStream())
                fillPJL(output, filename, paperSize, copies)
                //output.flush()
                it.onSuccess("Success printing")
            } catch (e: IOException) {
                it.onError(e)
            }
        }
    }

    private fun fillPJL(output: DataOutputStream?, filename: String, paperSize: Paper, copies: Int) {
        OutputHelper.writeHeader(output, filename, paperSize, copies)
        writeData(output)
        OutputHelper.writeFooter(output)
    }
}

enum class Paper {
    A4, A5
}
