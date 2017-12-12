package com.example.print.printerserver.adapters

import com.example.print.printerserver.PrintAdapter
import com.example.print.printerserver.write
import java.io.File
import java.io.FileDescriptor
import java.io.FileOutputStream

internal class FilePrintAdapter(private val file: File) : PrintAdapter() {

    override fun write(fileDescriptor: FileDescriptor) {
        FileOutputStream(fileDescriptor).write(file)
    }
}