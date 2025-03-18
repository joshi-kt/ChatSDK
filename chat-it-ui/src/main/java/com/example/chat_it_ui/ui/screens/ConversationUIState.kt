package com.example.chat_it_ui.ui.screens

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import com.example.chat_it_ui.ui.models.MessageUIModel

@Stable
data class ConversationUIState(
    val isLoading : Boolean = false,
    val messages : List<MessageUIModel> = emptyList(),
    val currentPlaying : String? = null,
    val internetConnectivity : Boolean = true
)