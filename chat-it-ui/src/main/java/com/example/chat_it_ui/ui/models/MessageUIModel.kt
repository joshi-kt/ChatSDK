package com.example.chat_it_ui.ui.models

import com.example.chat_it.model.Message
import com.example.chat_it.model.MessageSender
import com.example.chat_it.model.MessageStatus
import com.example.chat_it.model.MessageType
import kotlinx.serialization.Serializable
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.UUID

@Serializable
data class MessageUIModel(
    val messageId : String,
    val message : String,
    val timeStamp: DisplayableDate,
    val messageType: MessageType,
    val messageStatus: MessageStatus,
    val messageSender: MessageSender,
    val messageMetadata: Map<String,String>
) {

    fun isUserMessage() = this.messageSender == MessageSender.USER

    fun isAudioMessage() = this.messageType == MessageType.AUDIO

    fun isTextMessage() = this.messageType == MessageType.TEXT

    fun getAudioLocalPath() = this.messageMetadata["local_file_path"]

}

@Serializable
data class DisplayableDate(
    val timeInLong: Long,
    val dateAndTime: String = SimpleDateFormat("MM/dd/yyyy hh:mm:ss a",
        Locale.getDefault()).format(timeInLong),
    val time : String = SimpleDateFormat("hh:mm a",
        Locale.getDefault()).format(timeInLong)
)