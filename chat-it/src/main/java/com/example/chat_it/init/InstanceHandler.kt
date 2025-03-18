package com.example.chat_it.init

import android.content.Context
import com.example.chat_it.helper.ChatItHelper
import com.example.chat_it.util.Logger
import com.example.chat_it.util.SDKUninitializedException

object InstanceHandler {

    private const val TAG = "InstanceHandler"

    @Volatile
    private var _instance: ChatItInstance? = null

    val instance: ChatItInstance
        get() {
            return _instance ?: synchronized(this) {
                return _instance ?: throw SDKUninitializedException()
            }
        }

    fun createInstance(
        context: Context,
        appKey: String,
        localConfig: LocalConfig
    ) {
        synchronized(this) {
            if (_instance == null) {
//                ChatItHelper.clearOlderFiles(
//                    context,
//                    onCleared = {
//                        _instance = ChatItInstance(appKey, localConfig)
//                        Logger.log(TAG,Logger.LogType.DEBUG,"Instance initialized : $_instance")
//                    }
//                )
                _instance = ChatItInstance(appKey, localConfig)
            } else {
                Logger.log(TAG,Logger.LogType.DEBUG,"Instance already initialized")
            }
        }
    }

    fun ensureInitialized() {
        if (_instance == null) {
            throw SDKUninitializedException("ChatItInstance has not been created. Call createInstance first.")
        }
    }

}