package dev.sangui.wakeupright.utils

import android.util.Log

class Event<T>(private val content: T) {
    private var hasBeenHandled = false

    fun getContentIfNotHandled(): T? {

        return if (!hasBeenHandled) {
            Log.d("testing", "hasBeenHandled")

            hasBeenHandled = true
            content
        } else {
            Log.d("testing", "HAS NOT hasBeenHandled")

            null
        }
    }
}