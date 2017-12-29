package ru.alexeyp.printerserver.connectors

import ru.alexeyp.printerserver.utils.write
import java.io.DataOutputStream
import java.io.InputStream

internal class StreamPrinterConnector(private val data: InputStream) : PrinterConnector() {

    override fun writeData(output: DataOutputStream?) {
        output?.write(data)
    }
}