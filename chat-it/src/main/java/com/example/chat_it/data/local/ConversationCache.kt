package com.example.chat_it.data.local

import com.example.chat_it.helper.MessagesUpdateListener
import com.example.chat_it.model.Message
import com.example.chat_it.model.MessageStatus
import com.example.chat_it.util.Logger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import java.util.UUID

internal object ConversationCache {

    val messagesFlow = MutableStateFlow<List<Message>>(emptyList())

    private val listeners = mutableListOf<MessagesUpdateListener>()

    const val TAG = "ConversationCache"

    fun addNewMessage(message: Message) {
        synchronized(this) {
            Logger.log(TAG, Logger.LogType.DEBUG, "adding new message to list $message")
            val messageList = messagesFlow.updateAndGet { currentMessages ->
                (currentMessages + message)
            }
            notifyListeners(messageList)
        }
    }

    fun deleteMessage(messageId: String) {
        synchronized(this) {
            Logger.log(TAG, Logger.LogType.DEBUG, "deleting message with id : $messageId")
            val messageList = messagesFlow.updateAndGet { messages ->
                messages.filter { message ->
                    message.messageId != messageId
                }
            }
            notifyListeners(messageList)
        }
    }

    fun updateMessageStatus(messageId: String, messageStatus: MessageStatus) {
        synchronized(this) {
            Logger.log(TAG, Logger.LogType.DEBUG, "updating message status with id : $messageId to status : $messageStatus")
            val messageList = messagesFlow.updateAndGet { messages ->
                messages.map { message ->
                    if (message.messageId == messageId) {
                        Logger.log(TAG, Logger.LogType.DEBUG, "updated message status with id : $messageId from status : ${message.messageStatus} to $messageStatus")
                        message.copy(messageStatus = messageStatus)
                    } else {
                        message
                    }
                }
            }
            notifyListeners(messageList)
        }
    }

    fun updateMessageMetadata(messageId: String, messageMetadata : Map<String,String>) {
        synchronized(this) {
            Logger.log(TAG, Logger.LogType.DEBUG, "updating message metadata with id : $messageId to metadata : $messageMetadata")
            val messageList = messagesFlow.updateAndGet { messages ->
                messages.map { message ->
                    if (message.messageId == messageId) {
                        Logger.log(TAG, Logger.LogType.DEBUG, "updated message status with id : $messageId from status : ${message.messageMetadata} to $messageMetadata")
                        message.copy(messageMetadata = messageMetadata)
                    } else {
                        message
                    }
                }
            }
            notifyListeners(messageList)
        }
    }

    fun updateMessageProcessingTimestamp(messageId: String, messageTimestamp: Long = -1) {
        synchronized(this) {
            Logger.log(TAG, Logger.LogType.DEBUG, "updating message timestamp with id : $messageId to timestamp : $messageTimestamp")
            val messageList = messagesFlow.updateAndGet { messages ->
                messages.map { message ->
                    if (message.messageId == messageId) {
                        Logger.log(TAG, Logger.LogType.DEBUG, "updated message status with id : $messageId from status : ${message.messageStatus} to $messageTimestamp")
                        message.copy(
                            timeStamp = message.timeStamp.copy(
                                executedTimeStamp = if(messageTimestamp == -1L) System.currentTimeMillis() else messageTimestamp
                            )
                        )
                    } else {
                        message
                    }
                }
            }
            notifyListeners(messageList)
        }
    }

    fun addListener(listener: MessagesUpdateListener) {
        Logger.log(TAG, Logger.LogType.DEBUG, "attaching messages list listener with listenerId : ${listener.listenerId}")
        listeners.add(listener)
    }

    fun removeListener(listener: MessagesUpdateListener) {
        Logger.log(TAG, Logger.LogType.DEBUG, "removing messages list listener with listenerId : ${listener.listenerId}")
        listeners.remove(listener)
    }

    private fun notifyListeners(messageList : List<Message>) {
        listeners.forEach {
            Logger.log(TAG, Logger.LogType.DEBUG, "notifying messages list listener with listenerId : ${it.listenerId}")
            it.onMessagesUpdated(messageList)
        }
    }

    fun getMessages(): List<Message> = messagesFlow.value

    fun getMessagesByProcessingTime() = messagesFlow.value.sortedBy { it.timeStamp.executedTimeStamp }

    fun getAllMessagesAsFlow() : Flow<List<Message>> = messagesFlow.asStateFlow()

    fun clearMessages() {
        synchronized(this) {
            val list = emptyList<Message>()
            messagesFlow.update { list }
            notifyListeners(list)
        }
    }

}