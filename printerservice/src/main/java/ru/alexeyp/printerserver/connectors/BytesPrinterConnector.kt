package ru.alexeyp.printerserver.connectors

import java.io.DataOutputStream

internal class BytesPrinterConnector(private val data: ByteArray) : PrinterConnector() {

    override fun writeData(output: DataOutputStream?) {
        output?.write(data)
    }
}