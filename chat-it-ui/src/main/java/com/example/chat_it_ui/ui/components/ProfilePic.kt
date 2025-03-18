package com.example.chat_it_ui.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.chat_it.R
import com.example.chat_it.util.DEFAULT_IMAGE
import com.example.chat_it_ui.ui.theme.primary
import com.example.chat_it_ui.utils.Utils

@Composable
fun ProfilePic(
    imageDrawable : Int,
    contentDescription: String,
    sizeInDp : Dp
) {

    if (imageDrawable != DEFAULT_IMAGE) {
        Image(
            painter = painterResource(imageDrawable),
            contentDescription = stringResource(id = R.string.bot_image),
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .size(sizeInDp)
                .clip(CircleShape)
                .border(2.dp, primary, CircleShape)
                .padding(1.dp)
        )
    } else {
        Image(
            imageVector = Icons.Filled.Person,
            contentDescription = stringResource(id = R.string.bot_image),
            colorFilter = ColorFilter.tint(
                color = Utils.getBlackOrWhite()
            ),
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .size(sizeInDp)
                .clip(CircleShape)
                .border(2.dp, primary, CircleShape)
                .padding(3.dp)
        )
    }
}