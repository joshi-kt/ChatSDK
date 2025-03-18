package com.example.chat_it_ui.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chat_it.R
import com.example.chat_it_ui.ui.screens.ConversationScreenAction
import com.example.chat_it_ui.ui.screens.ConversationUIState
import com.example.chat_it_ui.ui.theme.DarkWhite
import com.example.chat_it_ui.ui.theme.LightBlack
import com.example.chat_it_ui.ui.theme.primary
import com.example.chat_it_ui.utils.Utils

@Composable
fun BottomBar(
    onSendButtonClicked: (String) -> Unit,
    onAudioButtonClicked: () -> Unit,
) {

    var messageText by rememberSaveable {
        mutableStateOf("")
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Max)
            .background(
                color = Utils.getWhiteOrBlack()
            )
            .padding(
                start = 2.dp,
                end = 2.dp
            ),
    ) {

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxHeight(),
            content = {

                Image(
                    painter = painterResource(R.drawable.audio_file),
                    contentDescription = "Send Audio Message",
                    colorFilter = ColorFilter.tint(
                        color = Utils.getBlackOrWhite()
                    ),
                    modifier = Modifier
                        .size(32.dp)
                        .clickable {
                            onAudioButtonClicked()
                        }
                )

            }
        )

        Box(
            modifier = Modifier
                .weight(1f),
            content = {
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = messageText,
                    onValueChange = {
                        messageText = it
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Utils.getWhiteOrBlack(),
                        unfocusedContainerColor = Utils.getWhiteOrBlack(),
                        unfocusedIndicatorColor = Utils.getWhiteOrBlack(),
                        focusedIndicatorColor = Utils.getWhiteOrBlack(),
                        unfocusedTextColor = Utils.getBlackOrWhite(),
                        focusedTextColor = Utils.getBlackOrWhite()
                    ),
                    placeholder = {
                        Text(
                            text = "Type a message",
                            color = Utils.getLightOrDarkColor(
                                lightColor = LightBlack.copy(alpha = 0.8f),
                                darkColor = DarkWhite.copy(alpha = 0.8f)
                            ),
                            fontSize = 12.sp
                        )
                    }
                )
            }
        )

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxHeight(),
            content = {

                Image(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send Message",
                    colorFilter = ColorFilter.tint(
                        color = Utils.getBlackOrWhite()
                    ),
                    modifier = Modifier
                        .size(30.dp)
                        .clickable {
                            if(messageText.trim().isNotBlank()) {
                                onSendButtonClicked(messageText)
                                messageText = ""
                            }
                        }
                )

            }
        )

    }

}

@PreviewLightDark
@Composable
fun BottomBarPreview(modifier: Modifier = Modifier) {
    BottomBar({},{})
}