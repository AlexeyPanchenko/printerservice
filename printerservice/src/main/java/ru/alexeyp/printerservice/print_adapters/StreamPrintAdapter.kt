package ru.alexeyp.printerservice.print_adapters

import ru.alexeyp.printerservice.utils.write
import java.io.FileDescriptor
import java.io.FileOutputStream
import java.io.InputStream

internal class StreamPrintAdapter(private val data: InputStream) : PrintAdapter() {

    override fun write(fileDescriptor: FileDescriptor) {
        FileOutputStream(fileDescriptor).write(data)
    }
}