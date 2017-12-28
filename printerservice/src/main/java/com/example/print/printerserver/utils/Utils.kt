package com.example.print.printerserver.utils

import android.util.Log
import java.io.File
import java.io.InputStream
import java.io.OutputStream

internal fun OutputStream.write(inputStream: InputStream) {
    inputStream.use { input ->
        this.use { it.write(input.readBytes()) }
    }
}

internal fun OutputStream.write(file: File) {
    file.inputStream().use { input ->
        this.use { it.write(input.readBytes()) }
    }
}

fun log(message: String, tag: String = "PrinterService") {
    Log.d(tag, "┌================================================================================")
    Log.d(tag, "│$message")
    Log.d(tag, "└================================================================================")
}
