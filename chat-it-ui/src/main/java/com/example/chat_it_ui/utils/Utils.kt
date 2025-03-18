package com.example.chat_it_ui.utils

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.chat_it.model.MessageSender
import com.example.chat_it.model.MessageStatus
import com.example.chat_it.model.MessageType

object Utils {

    @Composable
    inline fun getLightOrDarkColor(lightColor : Color, darkColor : Color) = if (isSystemInDarkTheme()) darkColor else lightColor

    @Composable
    inline fun getWhiteOrBlack() = if (isSystemInDarkTheme()) Color.Black else Color.White

    @Composable
    inline fun getBlackOrWhite() = if (isSystemInDarkTheme()) Color.White else Color.Black

    fun dummyData() = listOf("hello myself himanshu, i study in class 10th, my last name is joshi, my nationality is indian","what's my name","what's my class","what's my last name","what's my nationality","hi","hi").mapIndexed { index,value ->
        value.toMessageUIModel(
            messageType = MessageType.TEXT,
            messageSender = if ( index.mod(2) == 0 ) MessageSender.USER else MessageSender.BOT,
            messageStatus = MessageStatus.SENT
        )
    }


}