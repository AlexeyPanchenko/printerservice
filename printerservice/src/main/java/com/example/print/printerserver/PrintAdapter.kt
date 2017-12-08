package com.example.print.printerserver

import android.os.Bundle
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentInfo
import java.io.FileOutputStream
import java.io.InputStream

internal class PrintAdapter(private val inputStream: InputStream) : PrintDocumentAdapter() {

    override fun onLayout(oldAttributes: PrintAttributes?, newAttributes: PrintAttributes, cancellationSignal: CancellationSignal, callback: LayoutResultCallback, extras: Bundle?) {
        if (cancellationSignal.isCanceled) {
            callback.onLayoutCancelled()
            return
        }
        val pages = computePageCount(newAttributes)
        if (pages > 0) {
            val info = PrintDocumentInfo.Builder("file.pdf")
                    .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                    .setPageCount(pages)
                    .build()
            callback.onLayoutFinished(info, false)
        } else {
            callback.onLayoutFailed("Page count calculation failed.")
        }
    }

    override fun onWrite(pages: Array<out PageRange>?, destination: ParcelFileDescriptor, cancellationSignal: CancellationSignal?, callback: WriteResultCallback) {
        FileOutputStream(destination.fileDescriptor).write(inputStream)
        callback.onWriteFinished(arrayOf(PageRange.ALL_PAGES))
    }

    private fun computePageCount(printAttributes: PrintAttributes): Int {
        var itemsPerPage = 1

        val pageSize = printAttributes.mediaSize
        if (!pageSize!!.isPortrait) {
            itemsPerPage = 1
        }
        val printItemCount = getPrintItemCount()
        return Math.ceil((printItemCount / itemsPerPage).toDouble()).toInt()
    }

    private fun getPrintItemCount() = 1
}