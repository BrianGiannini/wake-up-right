package dev.sangui.wakeupright.utils

import android.util.Log

class Event<T>(private val content: T) {
    private var hasBeenHandled = false

    fun getContentIfNotHandled(): T? {
        Log.d("Event", "Event handled status: $hasBeenHandled")

        return if (!hasBeenHandled) {
            hasBeenHandled = true
            Log.d("Event", "Handling event with content: $content")

            content
        } else {
            Log.d("Event", "HAS NOT hasBeenHandled")

            null
        }
    }

    fun peekContent(): T = content

}