package ru.alexeyp.printerserver.print_adapters

import ru.alexeyp.printerserver.utils.write
import java.io.File
import java.io.FileDescriptor
import java.io.FileOutputStream

internal class FilePrintAdapter(private val data: File) : PrintAdapter() {

    override fun write(fileDescriptor: FileDescriptor) {
        FileOutputStream(fileDescriptor).write(data)
    }
}