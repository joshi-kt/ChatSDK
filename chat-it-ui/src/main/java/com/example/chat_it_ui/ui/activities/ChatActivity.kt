package com.example.chat_it_ui.ui.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.chat_it_ui.ui.components.NoInternetDialog
import com.example.chat_it_ui.ui.screens.ConversationScreen
import com.example.chat_it_ui.ui.theme.ChatSDKTheme
import com.example.chat_it_ui.ui.viewmodels.ConversationScreenViewModel
import com.example.chat_it_ui.utils.ConnectivityObserver

class ChatActivity : ComponentActivity() {

    private val viewModel by viewModels<ConversationScreenViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            val uiState by viewModel.uiState.collectAsState()

            val context = LocalContext.current

            LaunchedEffect(Unit) {
                viewModel.startObservingInternet(context.applicationContext)
            }

            ChatSDKTheme {

                ConversationScreen(
                    uiState = uiState,
                    onAction = {
                        viewModel.onAction(action = it)
                    }
                )

                if (uiState.internetConnectivity.not()) {
                    NoInternetDialog(
                        title = "Connection Error",
                        message = "It seems there's an issue with your internet connection. Please check your settings or try again later.",
                    )
                }

            }
        }
    }
}