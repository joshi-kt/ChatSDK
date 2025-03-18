package com.example.chatsdk

import android.app.Application
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.chat_it.helper.ChatItHelper
import com.example.chat_it.init.ChatIt
import com.example.chat_it.init.LocalConfig
import kotlinx.coroutines.launch

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        ChatIt(
            context = this,
            appKey = "AIzaSyAeT3bUspMXf44tmjXqOeaNMAEZjv18dM8",
            localConfig = LocalConfig(
                botImage = R.drawable.img,
                userImage = R.drawable.user,
            )
        ).initialize()
    }
}