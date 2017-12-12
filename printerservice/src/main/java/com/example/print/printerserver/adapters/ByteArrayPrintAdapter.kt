package com.example.print.printerserver.adapters

import com.example.print.printerserver.PrintAdapter
import java.io.FileDescriptor
import java.io.FileOutputStream

class ByteArrayPrintAdapter(private val byteArray: ByteArray): PrintAdapter() {
    override fun write(fileDescriptor: FileDescriptor) {
        FileOutputStream(fileDescriptor).write(byteArray)
    }
}