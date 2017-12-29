package ru.alexeyp.printerservice.print_adapters

import java.io.FileDescriptor
import java.io.FileOutputStream

internal class ByteArrayPrintAdapter(private val data: ByteArray): PrintAdapter() {
    override fun write(fileDescriptor: FileDescriptor) {
        FileOutputStream(fileDescriptor).write(data)
    }
}