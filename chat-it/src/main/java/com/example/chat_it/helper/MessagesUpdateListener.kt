package com.example.chat_it.helper

import com.example.chat_it.model.Message

interface MessagesUpdateListener {

    var listenerId : String

    fun onMessagesUpdated(messages: List<Message>)

}