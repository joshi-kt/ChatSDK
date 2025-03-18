package com.example.chat_it.util

import com.example.chat_it.init.InstanceHandler
import com.example.chat_it.model.Message
import com.example.chat_it.model.MessageSender
import com.example.chat_it.model.MessageStatus
import com.example.chat_it.model.MessageType
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonObjectBuilder
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.add
import kotlinx.serialization.json.addJsonObject
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject

fun String.toMessage(messageType: MessageType, messageStatus: MessageStatus, messageSender: MessageSender) : Message {
    return Message(
        message = this,
        messageType = messageType,
        messageStatus = messageStatus,
        messageSender = messageSender
    )
}

fun List<Message>.toRequestBody() : String {

    return Json.encodeToString(
        buildJsonObject {
            addQueryPrompt()
            addContents(this@toRequestBody)
        }
    )
}