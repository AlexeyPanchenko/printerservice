package ru.alexeyp.printerservice.connectors

import ru.alexeyp.printerservice.utils.write
import java.io.DataOutputStream
import java.io.File

internal class FilePrinterConnector(private val data: File) : PrinterConnector() {

    override fun writeData(output: DataOutputStream?) {
        output?.write(data)
    }
}