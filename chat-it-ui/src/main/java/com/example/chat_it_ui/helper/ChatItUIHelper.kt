package com.example.chat_it_ui.helper

import android.content.Context
import android.content.Intent
import com.example.chat_it_ui.ui.activities.ChatActivity

object ChatItUIHelper {

    fun startChat(context : Context) {
        val intent = Intent(context, ChatActivity::class.java)
        context.startActivity(intent)
    }

}