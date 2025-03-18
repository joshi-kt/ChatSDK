package com.example.chat_it.model

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Message(
    var messageId : String = UUID.randomUUID().toString(),
    val message : String,
    val timeStamp: TimeStamp = TimeStamp(),
    val messageSender: MessageSender,
    var messageStatus: MessageStatus,
    val messageType : MessageType,
    val messageMetadata: Map<String,String> = mapOf()
) {

    fun isUserMessage() = this.messageSender == MessageSender.USER

    fun isTextMessage() = this.messageType == MessageType.TEXT

    fun isAudioMessage() = this.messageType == MessageType.AUDIO

    fun getAudioLocalPath() = this.messageMetadata["local_file_path"]

}

@Serializable
data class TimeStamp(
    val loggedTimeStamp : Long = System.currentTimeMillis(),
    val executedTimeStamp : Long = System.currentTimeMillis(),
)

enum class MessageStatus(val value: Int) {
    QUEUED(0),
    SENDING(1),
    SENT(2),
    FAILED(3),
    RECEIVED(4)
}

enum class MessageSender(val value: Int) {
    USER(0),
    BOT(1)
}

enum class MessageType(val value: Int) {
    TEXT(0),
    DATE(1),
    AUDIO(2)

}