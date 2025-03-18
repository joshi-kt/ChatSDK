package com.example.chat_it_ui.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.chat_it.R
import com.example.chat_it.init.ChatIt
import com.example.chat_it.init.InstanceHandler
import com.example.chat_it_ui.ui.components.BottomBar
import com.example.chat_it_ui.ui.components.MessageUIItem
import com.example.chat_it_ui.ui.components.ProfilePic
import com.example.chat_it_ui.ui.models.MessageUIModel
import com.example.chat_it_ui.ui.theme.DarkWhite
import com.example.chat_it_ui.ui.theme.LightBlack
import com.example.chat_it_ui.ui.theme.primary
import com.example.chat_it_ui.utils.Utils
import kotlinx.coroutines.delay

@Composable
fun ConversationScreen(
    modifier: Modifier = Modifier,
    uiState: ConversationUIState,
    onAction: (ConversationScreenAction) -> Unit
) {

    val context = LocalContext.current

    var showPermissionUI by rememberSaveable {
        mutableStateOf(false)
    }

    var openFileManager by rememberSaveable {
        mutableStateOf(false)
    }

    if (showPermissionUI) {
        PermissionUI(
            permissionResult = {
                if (it) openFileManager = true
                showPermissionUI = false
            }
        )
    }

    if (openFileManager) {
        AudioPickerUI(
            onAudioPicked = {
                it?.let {
                    onAction(ConversationScreenAction.OnAudioButtonClicked(
                        audioUri = it,
                        context = context
                    ))
                }
                openFileManager = false
            }
        )
    }

    Scaffold(

        modifier = Modifier
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .fillMaxSize(),

        containerColor = Utils.getLightOrDarkColor(
            lightColor = Color.White,
            darkColor = Color.Black
        ),

        topBar = {
            ToolBar()
        },

        content = { paddingValues ->

            Box(
                contentAlignment = Alignment.BottomStart,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ){

                if(uiState.messages.isNotEmpty()) {

                    MessagesListUI(
                        messages = uiState.messages,
                        uiState = uiState,
                        onPlayPauseButtonClicked = {
                            onAction(ConversationScreenAction.OnPlayPauseButtonClicked(it))
                        }
                    )

                }

            }

        },

        bottomBar = {

            Column {

                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(
                            color = primary
                        )
                )

                BottomBar(
                    onSendButtonClicked = {
                        onAction(ConversationScreenAction.OnSendButtonClicked(it))
                    },
                    onAudioButtonClicked = {
                        showPermissionUI = true
                    }
                )

            }

        }
    )

}

@Composable
fun PermissionUI(
    permissionResult : (Boolean) -> Unit
) {

    val storagePermissionResultLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            permissionResult(isGranted)
        }
    )

    val context = LocalContext.current

    val hasReadPermission = ContextCompat.checkSelfPermission(
        context,
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) Manifest.permission.READ_MEDIA_AUDIO
        else Manifest.permission.READ_EXTERNAL_STORAGE
    ) == PackageManager.PERMISSION_GRANTED

    LaunchedEffect(Unit) {
        if (hasReadPermission) permissionResult(true)
        else storagePermissionResultLauncher.launch(
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) Manifest.permission.READ_MEDIA_AUDIO
            else Manifest.permission.READ_EXTERNAL_STORAGE
        )
    }

}

@Composable
fun AudioPickerUI(
    onAudioPicked : (Uri?) -> Unit
) {

    val audioPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        onAudioPicked(uri)
    }

    LaunchedEffect(Unit) {
        audioPickerLauncher.launch("audio/*")
    }

}

@PreviewLightDark
@Composable
fun ConversationScreenPreview(){
    ChatIt(
        context = LocalContext.current,
        appKey = "AIzaSyCr3gedg1GjiptRwc2R5RmEuEjpWx2ZnD0",
    ).initialize()
    ConversationScreen(
        uiState = ConversationUIState(
            isLoading = false,
            messages = Utils.dummyData(),
            internetConnectivity = false
        ),
        onAction = {}
    )
}

@Composable
fun ToolBar(
    modifier: Modifier = Modifier
) {

    val localConfig = InstanceHandler.instance.localConfig

    Card(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = Utils.getLightOrDarkColor(
                lightColor = DarkWhite,
                darkColor = LightBlack
            )
        ),
        shape = RectangleShape,
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Max),
        content = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(
                    10.dp
                ),
                content = {

                    ProfilePic(
                        imageDrawable = localConfig.botImage,
                        contentDescription = stringResource(id = R.string.bot_image),
                        sizeInDp = 42.dp
                    )

                    Text(
                        text = InstanceHandler.instance.localConfig.botName,
                        color = Utils.getBlackOrWhite(),
                        modifier = Modifier.padding(
                            start = 10.dp
                        ),
                        fontSize = 20.sp,
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            )
        }
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MessagesListUI(
    messages : List<MessageUIModel>,
    uiState: ConversationUIState,
    onPlayPauseButtonClicked: (MessageUIModel) -> Unit
) {

    val listState = rememberLazyListState()

    val size = messages.size

    val isKeyboardVisible = WindowInsets.isImeVisible

    LaunchedEffect(size) {
        if (size > 0) {
            listState.animateScrollToItem(size - 1)
        }
    }

    // Adjust scrolling to ensure the most recent message remains visible when the keyboard opens. A slight delay is included to cater to animation timings of keyboard visibility changes.

    LaunchedEffect(isKeyboardVisible) {
        if (size > 0 && isKeyboardVisible) {
            val lastVisibleIndex = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1
            if (lastVisibleIndex == (size - 1)) {
                delay(100)
                listState.animateScrollToItem(size - 1)
            }
        }
    }

    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {

        items(
            count = messages.size,
            key = { messages[it].hashCode() },
            itemContent = {

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                MessageUIItem(
                    message = messages[it],
                    localConfig = InstanceHandler.instance.localConfig,
                    uiState = uiState,
                    onPlayPauseButtonClicked = {
                        onPlayPauseButtonClicked(it)
                    }
                )

                Spacer(
                    modifier = Modifier.height(6.dp)
                )

            }
        )

    }
}
