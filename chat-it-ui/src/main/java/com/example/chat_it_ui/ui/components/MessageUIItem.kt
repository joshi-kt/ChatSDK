package com.example.chat_it_ui.ui.components

import android.content.ClipDescription
import android.provider.MediaStore.Images
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateValueAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chat_it.R
//import com.example.chat_it_ui.
import com.example.chat_it.init.ChatIt
import com.example.chat_it.init.InstanceHandler
import com.example.chat_it.init.LocalConfig
import com.example.chat_it.model.MessageStatus
import com.example.chat_it.util.DEFAULT_IMAGE
import com.example.chat_it_ui.ui.models.MessageUIModel
import com.example.chat_it_ui.ui.screens.ConversationScreenAction
import com.example.chat_it_ui.ui.screens.ConversationUIState
import com.example.chat_it_ui.ui.theme.DarkWhite
import com.example.chat_it_ui.ui.theme.LightBlack
import com.example.chat_it_ui.ui.theme.primary
import com.example.chat_it_ui.utils.Utils

@Composable
fun MessageUIItem(
    message: MessageUIModel,
    localConfig: LocalConfig,
    uiState: ConversationUIState,
    onPlayPauseButtonClicked: (MessageUIModel) -> Unit
) {

    Row(
        horizontalArrangement = if (message.isUserMessage()) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(
                start = 5.dp,
                end = 5.dp
            )
    ) {

        if(!message.isUserMessage()) {

            ProfilePic(
                localConfig.botImage,
                LocalContext.current.getString(R.string.bot_image),
                sizeInDp = 32.dp
            )

            Spacer(
                modifier = Modifier.width(5.dp)
            )

        }

        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .clip(
                    RoundedCornerShape(
                        topStart = 40f,
                        topEnd = 40f,
                        bottomStart = if (message.isUserMessage()) 40f else 0f,
                        bottomEnd = if (message.isUserMessage()) 0f else 40f
                    )
                )
                .width(
                    IntrinsicSize.Max
                )
                .widthIn(
                    max = LocalConfiguration.current.screenWidthDp.dp.times(0.6f)
                )
                .background(
                    if (message.isUserMessage()) primary
                    else Utils.getLightOrDarkColor(
                        lightColor = DarkWhite,
                        darkColor = LightBlack
                    )
                )
                .padding(8.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        if (message.isAudioMessage()) {
                            onPlayPauseButtonClicked(message)
                        }
                    }
            ) {

                if (message.isAudioMessage()) {

                    Image(
                        painter = painterResource(
                            if (uiState.currentPlaying == message.messageId)
                                R.drawable.pause
                            else
                                R.drawable.play
                        ),
                        contentDescription = stringResource(id = R.string.play),
                        contentScale = ContentScale.FillBounds,
                        modifier = Modifier
                            .size(32.dp)
                            .padding(1.dp),
                        colorFilter = ColorFilter.tint(
                            color = Color.White
                        )
                    )

                }


                Text(
                    text = message.message,
                    color = if (message.isUserMessage()) Color.White
                    else Utils.getLightOrDarkColor(
                        lightColor = LightBlack,
                        darkColor = DarkWhite
                    ),
                    fontSize = 14.sp,
                    fontFamily = FontFamily.SansSerif,
                    modifier = Modifier.fillMaxWidth()
                )

            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {

                Text(
                    text = message.timeStamp.time,
                    color = if (message.isUserMessage()) Color.White
                    else Utils.getLightOrDarkColor(
                        lightColor = LightBlack,
                        darkColor = DarkWhite
                    ),
                    fontSize = 10.sp,
                    fontFamily = FontFamily.SansSerif,
                    textAlign = TextAlign.End,
                    modifier = Modifier
                        .weight(1f)
                )

                if (message.isUserMessage()) {

                    Image(
                        painter = painterResource(
                            when(message.messageStatus) {
                                MessageStatus.QUEUED -> R.drawable.sending
                                MessageStatus.SENDING -> R.drawable.sending
                                MessageStatus.SENT -> R.drawable.sent
                                MessageStatus.FAILED -> R.drawable.failed
                                MessageStatus.RECEIVED -> -1
                            }
                        ),
                        contentDescription = stringResource(id = R.string.bot_image),
                        colorFilter = ColorFilter.tint(
                            color = if (message.messageStatus == MessageStatus.FAILED) Color.Red else Color.White
                        ),
                        contentScale = ContentScale.FillBounds,
                        modifier = Modifier
                            .padding(
                                start = 4.dp
                            )
                            .size(14.dp)
                    )

                }

            }

        }


        if(message.isUserMessage()) {

            Spacer(
                modifier = Modifier.width(5.dp)
            )

            ProfilePic(
                localConfig.userImage,
                LocalContext.current.getString(R.string.user_image),
                sizeInDp = 32.dp
            )

        }

    }


}

@Preview
@Composable
private fun MessageUIItemPreview() {
    ChatIt(
        context = LocalContext.current,
        appKey = "AIzaSyCr3gedg1GjiptRwc2R5RmEuEjpWx2ZnD0",
    ).initialize()
    MessageUIItem(
        message = Utils.dummyData()[1],
        localConfig = InstanceHandler.instance.localConfig,
        uiState = ConversationUIState(),
        onPlayPauseButtonClicked = {}
    )
}
