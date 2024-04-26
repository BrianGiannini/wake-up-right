package dev.sangui.wakeupright.ui.mainmenu

import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import dev.sangui.wakeupright.alarm.AlarmItem
import dev.sangui.wakeupright.utils.Event
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SetupClockViewModel(application: Application) : AndroidViewModel(application) {
    private val _permissionRequest = MutableStateFlow<Event<String>?>(null)
    val permissionRequest: StateFlow<Event<String>?> = _permissionRequest.asStateFlow()

    init {
        // For debugging
        _permissionRequest.value = Event("Initial Debug Event")
    }

    fun checkAndRequestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmPermission = android.Manifest.permission.SCHEDULE_EXACT_ALARM
            if (ContextCompat.checkSelfPermission(getApplication(), alarmPermission) != PackageManager.PERMISSION_GRANTED) {
                _permissionRequest.value = Event(alarmPermission)
                Log.d("testing", "pass not granted ${_permissionRequest.value}")
            } else {
                // Permission already granted, proceed to set an exact alarm
                Log.d("testing", "pass granted")

                scheduleAlarm()
            }
        } else {
            // For API levels below 31, no need for exact alarm permission, directly schedule the alarm
            scheduleAlarm()
        }
    }

    fun debugTriggerPermissionRequest() {
        _permissionRequest.value = Event("Debug Permission Request: ${System.currentTimeMillis()}")
        Log.d("testing", "debugTriggerPermissionRequest")

    }

    private fun scheduleAlarm() {
//        alarmScheduler.schedule(AlarmItem())

    }

    fun handlePermissionResult(granted: Boolean) {
        if (granted) {
            Log.d("testing", "handle permission granted")

            scheduleAlarm()
        } else {
            Log.d("testing", "handle permission NOT granted")

            // Handle the case where permission is not granted
            // You might want to notify the user or disable certain features
        }
    }


}