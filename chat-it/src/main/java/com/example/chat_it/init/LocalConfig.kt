package com.example.chat_it.init

import androidx.annotation.DrawableRes
import com.example.chat_it.util.DEFAULT_AUDIO_MESSAGE_OUTPUT_INSTRUCTION
import com.example.chat_it.util.DEFAULT_BOT_NAME
import com.example.chat_it.util.DEFAULT_IMAGE
import com.example.chat_it.util.DEFAULT_PROMPT
import kotlinx.serialization.Serializable

@Serializable
data class LocalConfig(
    val queryPrompt : String = DEFAULT_PROMPT,
    @DrawableRes val botImage : Int = DEFAULT_IMAGE,
    @DrawableRes val userImage : Int = DEFAULT_IMAGE,
    val botName : String = DEFAULT_BOT_NAME,
    val botAudioQueryPrompt : String = DEFAULT_AUDIO_MESSAGE_OUTPUT_INSTRUCTION,
)