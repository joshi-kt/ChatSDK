package com.example.chat_it.init

import kotlinx.serialization.Serializable

@Serializable
data class ChatItInstance(
    val appKey : String,
    val localConfig: LocalConfig
)