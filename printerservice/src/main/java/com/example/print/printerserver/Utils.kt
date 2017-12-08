package com.example.print.printerserver

import java.io.FileOutputStream
import java.io.InputStream

internal fun FileOutputStream.write(inputStream: InputStream) {
    inputStream.use { input ->
        this.use { it.write(input.readBytes()) }
    }
}
