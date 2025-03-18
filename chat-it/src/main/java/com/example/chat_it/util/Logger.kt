package com.example.chat_it.util

import android.util.Log

internal object Logger {

    private const val LOGGER_TAG = "ChatItLogger"

    enum class LogType {
        DEBUG,
        ERROR
    }

    fun log(
        tag : String,
        logType: LogType,
        message : String
    ) {
        when(logType){
            LogType.DEBUG -> {
                Log.d(LOGGER_TAG, "$tag $message")
            }
            LogType.ERROR -> {
                Log.e(LOGGER_TAG, "$tag $message")
            }
        }
    }

}