package com.example.chat_it.data

import com.example.chat_it.data.handler.UserChatHandler
import com.example.chat_it.data.remote.RemoteMessageRepository

internal object ChatServicesProvider {

    val remoteMessageRepository: RemoteMessageRepository by lazy {
        RemoteMessageRepository()
    }

    val userChatHandler : UserChatHandler by lazy {
        UserChatHandler()
    }

}