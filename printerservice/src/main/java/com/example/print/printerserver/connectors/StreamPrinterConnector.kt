package com.example.print.printerserver.connectors

import com.example.print.printerserver.utils.write
import java.io.DataOutputStream
import java.io.InputStream

internal class StreamPrinterConnector(private val data: InputStream) : PrinterConnector() {

    override fun writeData(output: DataOutputStream?) {
        output?.write(data)
    }
}