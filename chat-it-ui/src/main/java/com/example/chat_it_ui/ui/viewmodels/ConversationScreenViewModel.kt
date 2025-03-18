package com.example.chat_it_ui.ui.viewmodels

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chat_it.helper.ChatItHelper
import com.example.chat_it.model.Message
import com.example.chat_it_ui.media.MediaHandler
import com.example.chat_it_ui.ui.models.MessageUIModel
import com.example.chat_it_ui.ui.screens.ConversationScreenAction
import com.example.chat_it_ui.ui.screens.ConversationUIState
import com.example.chat_it_ui.utils.ConnectivityObserver
import com.example.chat_it_ui.utils.toMessageUIModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

internal class ConversationScreenViewModel : ViewModel() {

    private val mutex = Mutex()
    private val _chatMessages = ChatItHelper.getAllMessagesAsFlow()

    private val _uiState = MutableStateFlow(ConversationUIState())
    val uiState = _uiState.asStateFlow()

    init {
        observeMessages()
        observeAudioFinishing()
    }

    private fun observeAudioFinishing() {
        MediaHandler.setOnCompletionListener {
            updateCurrentPlayingUIState()
        }
    }

    fun startObservingInternet(context: Context) {
        executeAsync(
            onBackgroundThread = false,
            action = {
                ConnectivityObserver.observeConnectivity(context).collectLatest {
                    if (it != _uiState.value.internetConnectivity)
                        updateConnectivityState(it)
                }
            }
        )
    }

    private fun updateConnectivityState(state : Boolean) {
        _uiState.update {
            it.copy(
                internetConnectivity = state
            )
        }
    }

    fun onAction(action: ConversationScreenAction){
        when(action){
            is ConversationScreenAction.OnAudioButtonClicked -> {
                sendAudioMessage(action.audioUri, action.context)
            }
            is ConversationScreenAction.OnSendButtonClicked -> {
                sendMessage(action.messageText)
            }

            is ConversationScreenAction.OnPlayPauseButtonClicked -> {
                if (MediaHandler.isPlaying) {
                    MediaHandler.pause()
                    updateCurrentPlayingUIState()
                } else {
                    MediaHandler.play(
                        messageUIModel = action.messageUIModel,
                        onStarted = {
                            updateCurrentPlayingUIState(messageId = action.messageUIModel.messageId)
                        }
                    )
                }
            }
        }
    }

    private fun updateCurrentPlayingUIState(messageId: String? = null) {
        _uiState.update {
            it.copy(
                currentPlaying = messageId
            )
        }
    }

    private fun sendMessage(messageText : String){
        executeAsync (
            action = {
                ChatItHelper.sendMessage(messageText)
            }
        )
    }

    private fun sendAudioMessage(
        audioUri: Uri,
        context: Context
    ){
        executeAsync (
            action = {
                ChatItHelper.sendAudioMessage(
                    audioUri = audioUri,
                    context = context
                )
            }
        )
    }

    private fun observeMessages() {
        executeAsync(
            action = {
                _chatMessages.collectLatest { messages ->
                    Log.d("ChatItLoggerMessages", messages.toString())
                    _uiState.update {
                        it.copy (
                            messages = messages.map { message -> message.toMessageUIModel() }
                        )
                    }
                }
            }
        )
    }

    private fun executeAsync(
        isSynchronized: Boolean = false,
        onBackgroundThread : Boolean = true,
        action: suspend () -> Unit,
        onError: (Throwable) -> Unit = { _ -> }
    ): Job {

        val handler = CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
            onError(throwable)
        }

        val dispatcher = if (onBackgroundThread) Dispatchers.IO else Dispatchers.Main

        return viewModelScope.launch(dispatcher + handler) {
            if (isSynchronized) {
                mutex.withLock {
                    action()
                }
            } else {
                action()
            }
        }
    }

    private suspend fun withMainContext(block: suspend () -> Unit) {
        withContext(Dispatchers.Main) {
            block()
        }
    }

    private suspend fun withIOContext(block: suspend () -> Unit) {
        withContext(Dispatchers.IO) {
            block()
        }
    }

    override fun onCleared() {
        MediaHandler.release()
        super.onCleared()
    }

}