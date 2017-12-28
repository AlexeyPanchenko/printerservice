package com.example.print.printerserver.print_adapters

import com.example.print.printerserver.utils.write
import java.io.FileDescriptor
import java.io.FileOutputStream
import java.io.InputStream

internal class StreamPrintAdapter(private val data: InputStream) : PrintAdapter() {

    override fun write(fileDescriptor: FileDescriptor) {
        FileOutputStream(fileDescriptor).write(data)
    }
}