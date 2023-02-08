@file:Suppress("unused")

package com.alfeugds.adskipper

import android.util.Log

const val LOG_ENABLED = false

inline fun <reified T> T.logMessage(message: String) {
    if (BuildConfig.DEBUG && LOG_ENABLED) {
        Log.d(T::class.java.simpleName, message)
    }
}

inline fun <reified T> T.logError(ex: Throwable, message: String? = null) {
    Log.e(T::class.java.simpleName, message, ex)
}
