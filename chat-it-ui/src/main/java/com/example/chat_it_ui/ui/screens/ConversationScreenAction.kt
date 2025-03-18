package com.example.chat_it_ui.ui.screens

import android.content.Context
import android.net.Uri
import com.example.chat_it_ui.ui.models.MessageUIModel

sealed interface ConversationScreenAction {
    data class OnAudioButtonClicked(val audioUri: Uri, val context: Context) : ConversationScreenAction
    data class OnSendButtonClicked(val messageText : String) : ConversationScreenAction
    data class OnPlayPauseButtonClicked(val messageUIModel: MessageUIModel) : ConversationScreenAction
}