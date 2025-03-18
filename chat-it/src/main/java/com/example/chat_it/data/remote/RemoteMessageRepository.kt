package com.example.chat_it.data.remote

import android.content.Context
import android.net.Uri
import android.provider.MediaStore.Audio
import android.view.WindowInsets
import androidx.annotation.WorkerThread
import com.example.chat_it.init.InstanceHandler
import com.example.chat_it.model.Message
import com.example.chat_it.model.MessageApiResponse
import com.example.chat_it.model.MessageSender
import com.example.chat_it.model.MessageStatus
import com.example.chat_it.model.MessageType
import com.example.chat_it.util.AI_MODEL
import com.example.chat_it.util.API_KEY_PARAM
import com.example.chat_it.util.API_VERSION
import com.example.chat_it.util.BASE_URL
import com.example.chat_it.util.CONTENTS
import com.example.chat_it.util.CREATE_FILE_ENDPOINT
import com.example.chat_it.util.GEMINI_MODEL
import com.example.chat_it.util.Logger
import com.example.chat_it.util.PARTS
import com.example.chat_it.util.ROLE
import com.example.chat_it.util.SEND_MESSAGE_ENDPOINT
import com.example.chat_it.util.SYSTEM_INSTRUCTION
import com.example.chat_it.util.TEXT
import com.example.chat_it.util.UPLOAD
import com.example.chat_it.util.USER
import com.example.chat_it.util.toMessage
import com.example.chat_it.util.toRequestBody
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.utils.EmptyContent.contentType
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.content.ByteArrayContent
import io.ktor.http.contentType
import io.ktor.http.headers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonBuilder
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.addJsonObject
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.putJsonArray

internal class RemoteMessageRepository {

    private val httpClient: HttpClient = HttpClientFactory.create(CIO.create{})
    private val TAG = "RemoteMessageRepository"

    suspend fun sendMessage(request: String) : Result<Message> {

        val url = constructSendMessageUrl()
        val apiKey = InstanceHandler.instance.appKey

        try {
            val res = httpClient.post(
                urlString = url,
            ) {

                url {
                    parameters.append(API_KEY_PARAM, apiKey)
                }

                setBody(request)

                Logger.log(
                    TAG,
                    Logger.LogType.DEBUG,
                    "Constructed URL for HTTP POST Request: ${this.url}"
                )

                Logger.log(
                    TAG,
                    Logger.LogType.DEBUG,
                    "Request Body for HTTP POST Request: ${this.body}"
                )
            }
            val response = res.body<MessageApiResponse>()
            val messageText = response.candidates[0].content.parts[0].text.trim()
            return Result.success(
                messageText.toMessage(
                    messageType = MessageType.TEXT,
                    messageStatus = MessageStatus.RECEIVED,
                    messageSender = MessageSender.BOT
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
            return Result.failure(e)
        }
    }

    @WorkerThread
    suspend fun createFileInServer(audioUri: Uri, context: Context, message: Message) : String {

        val apiKey = InstanceHandler.instance.appKey

        val mimeType = context.contentResolver.getType(audioUri)
        checkNotNull(mimeType) { "Unsupported media type" }
        check(mimeType.startsWith("audio/")) { "Unsupported Media Type" }

        val numBytes = context.contentResolver.openInputStream(audioUri)?.use {
            it.available()
        }
        checkNotNull(numBytes){"Corrupted file"}

        val url = constructCreateFileUrl()

        val displayName = message.message

        val headerResponse = httpClient.post(
            urlString = url,
        ) {

            url {
                parameters.append(API_KEY_PARAM, apiKey)
            }

            setBody(buildJsonObject {
                put("file", buildJsonObject {
                    put("display_name", JsonPrimitive(displayName))
                })
            })

            headers {
                append("X-Goog-Upload-Protocol", "resumable")
                append("X-Goog-Upload-Command", "start")
                append("X-Goog-Upload-Header-Content-Length", numBytes.toString())
                append("X-Goog-Upload-Header-Content-Type", mimeType)
                contentType(ContentType.Application.Json)
            }

            Logger.log(
                TAG,
                Logger.LogType.DEBUG,
                "Constructed URL for HTTP POST Request: ${this.url}"
            )

            Logger.log(
                TAG,
                Logger.LogType.DEBUG,
                "Request Body for HTTP POST Request: ${this.body}"
            )
        }

        return headerResponse.headers["X-Goog-Upload-URL"] ?: throw IllegalArgumentException("Upload Url not found")

    }

    @WorkerThread
    suspend fun uploadFileContentToServer(
        uploadUrl : String,
        audioUri: Uri,
        context: Context,
    ) : String {

        val mimeType = context.contentResolver.getType(audioUri).toString()

        val fileBytes = context.contentResolver.openInputStream(audioUri)?.use {
            it.readBytes()
        }
        checkNotNull(fileBytes) {"Corrupted file"}

        val result = httpClient.post(
            urlString = uploadUrl,
        ) {

            setBody(ByteArrayContent(fileBytes, ContentType.parse(mimeType)))

            headers {
                append(HttpHeaders.ContentLength, fileBytes.size.toString())
                append("X-Goog-Upload-Offset", "0")
                append("X-Goog-Upload-Command", "upload, finalize")
            }

            Logger.log(
                TAG,
                Logger.LogType.DEBUG,
                "Constructed URL for HTTP POST Request: ${this.url}"
            )

            Logger.log(
                TAG,
                Logger.LogType.DEBUG,
                "Request Body for HTTP POST Request: ${this.body}"
            )

        }

        val response = result.body<String>()
        val responseJson = Json.parseToJsonElement(response)
        return responseJson.jsonObject["file"]?.jsonObject?.get("uri")?.jsonPrimitive?.content ?: throw IllegalArgumentException("File upload failed")

    }

    suspend fun uploadAndGetResponse(audioUri: Uri, context: Context, geminiApiKey: String) {

        val mimeType = context.contentResolver.getType(audioUri) ?: "audio/*"
        val numBytes = context.contentResolver.openInputStream(audioUri)?.use {
            it.available()
        }

        val url = "${BASE_URL}/upload/v1beta/files?key=${geminiApiKey}"

        val displayName = "AUDIO"

        val headerResponse = httpClient.post(
            urlString = url,
        ) {

            setBody(buildJsonObject {
                put("file", buildJsonObject {
                    put("display_name", JsonPrimitive(displayName))
                })
            })

            headers {
                append("X-Goog-Upload-Protocol", "resumable")
                append("X-Goog-Upload-Command", "start")
                append("X-Goog-Upload-Header-Content-Length", numBytes.toString())
                append("X-Goog-Upload-Header-Content-Type", mimeType)
                contentType(ContentType.Application.Json)
            }

            Logger.log(
                TAG,
                Logger.LogType.DEBUG,
                "Constructed URL for HTTP POST Request: ${this.url}"
            )

            Logger.log(
                TAG,
                Logger.LogType.DEBUG,
                "Request Body for HTTP POST Request: ${this.body}"
            )
        }

        val uploadUrl = headerResponse.headers["X-Goog-Upload-URL"]

        val fileBytes = context.contentResolver.openInputStream(audioUri)?.readBytes()

        val file_info_json = httpClient.post(
            urlString = uploadUrl!!,
        ) {

            setBody(ByteArrayContent(fileBytes!!, ContentType.parse(mimeType)))

            headers {
                append(HttpHeaders.ContentLength, fileBytes.size.toString())
                append("X-Goog-Upload-Offset", "0")
                append("X-Goog-Upload-Command", "upload, finalize")
            }

            Logger.log(
                TAG,
                Logger.LogType.DEBUG,
                "Constructed URL for HTTP POST Request: ${this.url}"
            )

            Logger.log(
                TAG,
                Logger.LogType.DEBUG,
                "Request Body for HTTP POST Request: ${this.body}"
            )

        }.body<String>()

        val jsonElement = Json.parseToJsonElement(file_info_json)
        val file_uri = jsonElement.jsonObject["file"]?.jsonObject?.get("uri")?.jsonPrimitive?.content ?: ""

        val url1 = constructSendMessageUrl()
        val apiKey = InstanceHandler.instance.appKey

        val res = httpClient.post(
            urlString = url1,
        ) {

            url {
                parameters.append(API_KEY_PARAM, apiKey)
            }

            setBody(
                Json.encodeToString(
                    buildJsonObject {
                        putJsonArray("contents") {
                            addJsonObject {
                                putJsonArray("parts") {
                                    addJsonObject {
                                        put("text", JsonPrimitive("Describe this audio clip"))
                                    }
                                    addJsonObject {
                                        put("file_data", buildJsonObject {
                                            put("mime_type", JsonPrimitive(mimeType))
                                            put("file_uri", JsonPrimitive(file_uri))
                                        })
                                    }
                                }
                            }
                        }
                    }
                )
            )

            Logger.log(
                TAG,
                Logger.LogType.DEBUG,
                "Constructed URL for HTTP POST Request: ${this.url}"
            )

            Logger.log(
                TAG,
                Logger.LogType.DEBUG,
                "Request Body for HTTP POST Request: ${this.body}"
            )
        }
        val response = res.body<MessageApiResponse>()
        Logger.log(
            TAG,
            Logger.LogType.DEBUG,
            "Response Body for final POST Request: $response"
        )

        httpClient.close()
    }

    private fun constructSendMessageUrl() = "$BASE_URL$API_VERSION$GEMINI_MODEL$SEND_MESSAGE_ENDPOINT"

    private fun constructCreateFileUrl() = "$BASE_URL$UPLOAD$API_VERSION$CREATE_FILE_ENDPOINT"

}