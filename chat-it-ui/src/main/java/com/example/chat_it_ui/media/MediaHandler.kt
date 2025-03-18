package com.example.chat_it_ui.media

import android.media.MediaPlayer
import com.example.chat_it_ui.ui.models.MessageUIModel

object MediaHandler : MediaPlayer() {

    private var currentPlayingMessage : MessageUIModel? = null

    private fun prepare(
        filePath : String,
        onReady : () -> Unit
    ) {
        apply {
            setDataSource(filePath)
        }
        setOnPreparedListener {
            onReady()
        }
        prepareAsync()
    }

    fun play(
        onStarted: () -> Unit,
        messageUIModel: MessageUIModel,
        onError: (Throwable) -> Unit = {}
    ) {

        try {

            if(isCurrentPlaying(messageUIModel)) {
                start()
                onStarted()
                return
            }

            val filePath = messageUIModel.getAudioLocalPath()
            checkNotNull(filePath) { "Invalid Audio Path" }
            prepare(
                filePath = filePath,
                onReady = {
                    start()
                    updateCurrentPlaying(messageUIModel)
                    onStarted()
                }
            )

        } catch (e : Exception){
            e.printStackTrace()
            onError(e)
        }
    }

    private fun updateCurrentPlaying(messageUIModel: MessageUIModel) {
        currentPlayingMessage = messageUIModel
    }

    private fun isCurrentPlaying(messageUIModel: MessageUIModel) : Boolean {
        return currentPlayingMessage?.messageId == messageUIModel.messageId
    }

    fun currentQueuedAudio() : MessageUIModel? = currentPlayingMessage

}