package com.example.chat_it.data

import android.os.Looper
import com.example.chat_it.util.Logger
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.cancellation.CancellationException

object TaskExecutor {

    private val mutex = Mutex()
    const val TAG = "TaskExecutor"

    suspend fun execute(
        taskName: String,
        isSynchronized: Boolean = false,
        onBackgroundThread : Boolean = true,
        action: suspend () -> Unit,
        onError: (Throwable) -> Unit = { _ -> },
    ) {

        var coroutineContext : CoroutineContext = CoroutineExceptionHandler { _, throwable ->
            Logger.log(TAG, Logger.LogType.ERROR, "Error in $taskName : ${throwable.message}")
            throwable.printStackTrace()
            onError(throwable)
            if (throwable is CancellationException) throw throwable
        }

        coroutineContext +=
            if (onBackgroundThread && Looper.myLooper()?.equals(Looper.getMainLooper()) == true) Dispatchers.IO
            else Dispatchers.Main

        withContext(coroutineContext) {
            if (isSynchronized) {
                mutex.withLock {
                    action()
                }
            } else {
                action()
            }
            Logger.log(TAG, Logger.LogType.DEBUG, "$taskName execution completed")
        }

    }

    fun executeAsync(
        taskName: String,
        isSynchronized: Boolean = false,
        onBackgroundThread : Boolean = true,
        action: suspend () -> Unit,
        onError: (Throwable) -> Unit = { _ -> },
        onCompleted : ()-> Unit = {},
        coroutineScope: CoroutineScope = CoroutineScope(EmptyCoroutineContext)
    ) : Job {

        var coroutineContext : CoroutineContext = CoroutineExceptionHandler { _, throwable ->
            Logger.log(TAG, Logger.LogType.ERROR, "Error in $taskName : ${throwable.message}")
            throwable.printStackTrace()
            onError(throwable)
            if (throwable is CancellationException) throw throwable
        }

        coroutineContext +=
            if (onBackgroundThread && Looper.myLooper()?.equals(Looper.getMainLooper()) == true) Dispatchers.IO
            else Dispatchers.Main

        val job = coroutineScope.launch(coroutineContext) {
            if (isSynchronized) {
                mutex.withLock {
                    action()
                }
            } else {
                action()
            }
        }

        job.invokeOnCompletion {
            Logger.log(TAG, Logger.LogType.DEBUG, "$taskName execution completed")
            onCompleted()
        }

        return job

    }

}