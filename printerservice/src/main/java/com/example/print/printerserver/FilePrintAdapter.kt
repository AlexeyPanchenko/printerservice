package com.example.print.printerserver

import java.io.File
import java.io.FileDescriptor
import java.io.FileOutputStream

internal class FilePrintAdapter(private val file: File) : PrintAdapter() {

    override fun write(fileDescriptor: FileDescriptor) {
        FileOutputStream(fileDescriptor).write(file)
    }
}