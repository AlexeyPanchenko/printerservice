package com.example.print.printerserver.adapters

import com.example.print.printerserver.PrintAdapter
import com.example.print.printerserver.write
import java.io.FileDescriptor
import java.io.FileOutputStream
import java.io.InputStream

internal class StreamPrintAdapter(private val inputStream: InputStream) : PrintAdapter() {

    override fun write(fileDescriptor: FileDescriptor) {
        FileOutputStream(fileDescriptor).write(inputStream)
    }
}