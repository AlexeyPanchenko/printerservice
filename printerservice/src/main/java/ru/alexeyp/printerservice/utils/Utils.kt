package ru.alexeyp.printerservice.utils

import android.util.Log
import java.io.File
import java.io.InputStream
import java.io.OutputStream

val BUFFER = 8 * 1024

internal fun OutputStream.write(inputStream: InputStream) {
    inputStream.use { it.copyTo(this, bufferSize = BUFFER) }
}

internal fun OutputStream.write(file: File) {
    file.inputStream().use { it.copyTo(this, bufferSize = BUFFER) }
}

fun log(message: String, tag: String = "PrinterService") {
    Log.d(tag, "┌================================================================================")
    Log.d(tag, "│$message")
    Log.d(tag, "└================================================================================")
}
