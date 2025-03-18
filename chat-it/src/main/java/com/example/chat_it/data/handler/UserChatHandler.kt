package com.example.chat_it.data.handler

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import com.example.chat_it.data.ChatServicesProvider
import com.example.chat_it.data.TaskExecutor
import com.example.chat_it.data.local.ConversationCache
import com.example.chat_it.model.Message
import com.example.chat_it.model.MessageSender
import com.example.chat_it.model.MessageStatus
import com.example.chat_it.model.MessageType
import com.example.chat_it.util.CLOSE_CHAT_IT_TASK
import com.example.chat_it.util.CREATE_AUDIO_MESSAGE_TASK
import com.example.chat_it.util.Logger
import com.example.chat_it.util.SEND_AUDIO_MESSAGE_TASK
import com.example.chat_it.util.SEND_TEXT_MESSAGE_TASK
import com.example.chat_it.util.toRequestBody
import kotlinx.io.IOException
import kotlinx.io.files.FileNotFoundException

internal class UserChatHandler {

    private val repository = ChatServicesProvider.remoteMessageRepository
    private val TAG = "UserChatHandler"

    suspend fun sendMessage(messageText: String) {

        val message = Message(message = messageText, messageSender = MessageSender.USER, messageStatus = MessageStatus.QUEUED, messageType = MessageType.TEXT)
        Logger.log(TAG, Logger.LogType.DEBUG, "sending message $message")
        ConversationCache.addNewMessage(message)

        TaskExecutor.execute(
            taskName = SEND_TEXT_MESSAGE_TASK,
            isSynchronized = true,
            action = {
                ConversationCache.updateMessageStatus(message.messageId, MessageStatus.SENDING)
                ConversationCache.updateMessageProcessingTimestamp(message.messageId)
                val messageList = ConversationCache.getMessagesByProcessingTime()
                repository
                    .sendMessage(messageList.toRequestBody())
                    .onSuccess {
                        ConversationCache.updateMessageStatus(message.messageId, MessageStatus.SENT)
                        ConversationCache.addNewMessage(it)
                    }.onFailure {
                        ConversationCache.updateMessageStatus(
                            message.messageId,
                            MessageStatus.FAILED
                        )
                        it.printStackTrace()
                    }
            }
        )
    }

    suspend fun sendAudioMessage(
        audioUri : Uri,
        context : Context,
    ) {

        lateinit var message : Message

        TaskExecutor.execute(
            taskName = CREATE_AUDIO_MESSAGE_TASK,
            action = {
                check(AudioMessageHelper.isValidAudio(context, audioUri)) { "Unsupported audio file" }
                val fileName = AudioMessageHelper.getAudioFileNameFromUri(context, audioUri)
                message = Message(
                    message = fileName,
                    messageSender = MessageSender.USER,
                    messageStatus = MessageStatus.QUEUED,
                    messageType = MessageType.AUDIO
                )
                val localPath = AudioMessageHelper.saveAudioToDevice(audioUri,context,message)
                message = AudioMessageHelper.addAudioMetadata(message = message, filePath = localPath, context = context)
                Logger.log(TAG, Logger.LogType.DEBUG, "sending message $message")
                ConversationCache.addNewMessage(message)
            },
            onError = {
                if (it is IllegalStateException || it is FileNotFoundException || it is IOException) throw it
            }
        )

        TaskExecutor.execute(
            taskName = SEND_AUDIO_MESSAGE_TASK,
            isSynchronized = true,
            action = {
                ConversationCache.updateMessageStatus(message.messageId, MessageStatus.SENDING)
                ConversationCache.updateMessageProcessingTimestamp(message.messageId)
                val uploadUrl = repository.createFileInServer(audioUri, context, message)
                val uploadedUri = repository.uploadFileContentToServer(uploadUrl, audioUri, context)
                message = AudioMessageHelper.addAudioMetadata(
                    uploadedUri = uploadedUri,
                    message = message,
                    context = context,
                    audioUri = audioUri
                )
                ConversationCache.updateMessageMetadata(
                    message.messageId,
                    message.messageMetadata
                )
                val messageList = ConversationCache.getMessagesByProcessingTime()
                repository
                    .sendMessage(messageList.toRequestBody())
                    .onSuccess {
                        ConversationCache.updateMessageStatus(message.messageId, MessageStatus.SENT)
                        ConversationCache.addNewMessage(it)
                    }.onFailure {
                        ConversationCache.updateMessageStatus(
                            message.messageId,
                            MessageStatus.FAILED
                        )
                        it.printStackTrace()
                    }
            },
            onError = {
                ConversationCache.updateMessageStatus(message.messageId, MessageStatus.FAILED)
            }
        )
    }

}