package com.example.print.printerserver

import java.io.FileDescriptor
import java.io.FileOutputStream
import java.io.InputStream

internal class StreamPrintAdapter(private val inputStream: InputStream) : PrintAdapter() {

    override fun write(fileDescriptor: FileDescriptor) {
        FileOutputStream(fileDescriptor).write(inputStream)
    }
}