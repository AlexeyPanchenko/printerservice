package com.example.print.printerserver

import android.content.Context
import android.print.PrintManager
import android.support.v7.app.AppCompatActivity
import java.io.InputStream


interface IPrintService {
    fun print(inputStream: InputStream, name: String = "Document")
}

class PrintService(private val context: Context) : IPrintService {

    override fun print(inputStream: InputStream, name: String) {
        val printManager = context.getSystemService(AppCompatActivity.PRINT_SERVICE) as PrintManager
        printManager.print(name, PrintAdapter(inputStream), null)
    }
}