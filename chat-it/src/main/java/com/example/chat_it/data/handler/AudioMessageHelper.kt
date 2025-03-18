package com.example.chat_it.data.handler

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.provider.OpenableColumns
import androidx.annotation.WorkerThread
import com.example.chat_it.data.TaskExecutor
import com.example.chat_it.model.Message
import com.example.chat_it.util.CLOSE_CHAT_IT_TASK
import com.example.chat_it.util.Logger
import com.example.chat_it.util.MAX_FILE_SIZE
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

internal object AudioMessageHelper {

    private const val AUDIO_FOLDER_NAME = "Chat_It"
    private const val TAG = "AudioMessageHandler"

    @WorkerThread
    fun saveAudioToDevice(
        audioUri: Uri,
        context: Context,
        message: Message
    ) : String {
        val contentResolver = context.contentResolver
        val audioFolder = File(context.filesDir, AUDIO_FOLDER_NAME).apply {
            if (!exists()) mkdirs()
        }

        val localFile = File(audioFolder, message.messageId)

        contentResolver.openInputStream(audioUri)?.use { inputStream ->
            FileOutputStream(localFile).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }

        Logger.log(TAG, Logger.LogType.DEBUG, "Audio file saved to local storage: ${localFile.path}")

        return localFile.path

    }

    fun addAudioMetadata(uploadedUri: String? = null, message: Message, context: Context, audioUri: Uri? = null, filePath : String? = null) : Message {
        val metadata = message.messageMetadata.toMutableMap()
        uploadedUri?.let {
            metadata["file_uri"] = it
        }
        audioUri?.let {
            metadata["mime_type"] = context.contentResolver.getType(it).toString()
        }
        filePath?.let {
            metadata["local_file_path"] = it
        }
        return message.copy(
            messageMetadata = metadata.toMap()
        )
    }

    @WorkerThread
    fun getAudioFileNameFromUri(context: Context, audioUri: Uri): String {
        var fileName = ""
        context.contentResolver.query(audioUri, null, null, null, null)?.use {
            val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (it.moveToFirst()) {
                fileName = it.getString(nameIndex)
            }
        }

        return fileName.ifBlank { audioUri.lastPathSegment.toString() }
    }

    fun isValidAudio(context: Context, audioUri: Uri) : Boolean {

        val mimeType = context.contentResolver.getType(audioUri)
        mimeType?.let {
            if (it.startsWith("audio/")) return true
            val numBytes = context.contentResolver.openInputStream(audioUri)?.use { stream ->
                stream.available()
            }
            numBytes?.let {
                return numBytes <= MAX_FILE_SIZE
            } ?: return false

        } ?: return false

    }

    fun clearOlderFiles(context: Context, onCompleted : ()-> Unit = {}) {
        TaskExecutor.executeAsync(
            taskName = CLOSE_CHAT_IT_TASK,
            isSynchronized = true,
            action = {
                val audioFolder = File(context.filesDir, AUDIO_FOLDER_NAME)
                if (audioFolder.exists()) {
                    Logger.log(TAG, Logger.LogType.DEBUG, "Audio messages folder cleared. : ${audioFolder.deleteRecursively()}")
                }
            },
            onCompleted = {
                onCompleted()
            }
        )
    }

}