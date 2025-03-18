package com.example.chat_it.init

import android.content.Context
import com.example.chat_it.helper.ChatItHelper

class ChatIt(
    val context : Context,
    val appKey : String,
    val localConfig : LocalConfig = LocalConfig()
) {

    fun initialize() {
        initSDK()
    }

    private fun initSDK() {
        synchronized(this) {
            if (appKey.isBlank()) throw IllegalArgumentException("App key can't be blank")
            InstanceHandler.createInstance(
                context = context.applicationContext,
                appKey = appKey,
                localConfig = localConfig
            )
        }
    }

}