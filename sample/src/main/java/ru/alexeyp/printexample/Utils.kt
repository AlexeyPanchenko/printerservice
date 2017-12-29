package ru.alexeyp.printexample

import android.util.Log

fun log(message: String, tag: String = "LOGGER") {
    Log.d(tag, "┌================================================================================")
    Log.d(tag, "│$message")
    Log.d(tag, "└================================================================================")
}