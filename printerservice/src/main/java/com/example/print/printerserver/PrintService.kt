package com.example.print.printerserver

import android.content.Context
import android.print.PrintManager
import android.support.v7.app.AppCompatActivity
import java.io.File
import java.io.InputStream


interface IPrintService {
    fun print(inputStream: InputStream, name: String = "Document")
    fun print(file: File, name: String = "Document")
}

class PrintService(private val context: Context) : IPrintService {

    override fun print(inputStream: InputStream, name: String) {
        startPrint(name, StreamPrintAdapter(inputStream))
    }

    override fun print(file: File, name: String) {
        startPrint(name, FilePrintAdapter(file))
    }

    private fun startPrint(name: String, adapter: PrintAdapter) {
        val printManager = context.getSystemService(AppCompatActivity.PRINT_SERVICE) as PrintManager
        printManager.print(name, adapter, null)
    }
}