package dev.sangui.wakeupright.ui.mainmenu

import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.AndroidViewModel
import dev.sangui.wakeupright.alarm.AlarmItem
import dev.sangui.wakeupright.alarm.AlarmReceiver
import dev.sangui.wakeupright.alarm.AlarmScheduler
import dev.sangui.wakeupright.alarm.RingToneProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.java.KoinJavaComponent
import java.time.LocalDateTime

class SetupClockViewModel(
    application: Application,
    private val alarmScheduler: AlarmScheduler,
) : AndroidViewModel(application) {

    private var alarmItem: AlarmItem? = null
    private val _scheduledDate = MutableStateFlow<LocalDateTime?>(null)
    val scheduledDate: StateFlow<LocalDateTime?> get() = _scheduledDate.asStateFlow()
    private val ringToneProvider: RingToneProvider by KoinJavaComponent.inject(RingToneProvider::class.java)

    fun scheduleAlarm(hours: Int, minutes: Int) {
        val time = LocalDateTime.now().plusHours(hours.toLong()).plusMinutes(minutes.toLong())
        alarmItem = AlarmItem(
            alarmTime = time,
            message = "Wake up!",
        )
        try {
            alarmItem?.let(alarmScheduler::schedule)
            _scheduledDate.value = time
        } catch (e: Exception) {
            _scheduledDate.value = null
            Log.e("SetupClockViewModel", "Error scheduling alarm", e)
        }
    }

    fun cancelAlarm(context: Context) {
        try {
            alarmItem?.let {
                alarmScheduler.cancel(it.hashCode())
            }
            _scheduledDate.value = null

            // Intent to cancel vibration
            val cancelIntent = Intent(context, AlarmReceiver::class.java).apply {
                action = "CANCEL_RINGTONE"
            }
            val cancelPendingIntent = PendingIntent.getBroadcast(context, 0, cancelIntent, PendingIntent.FLAG_IMMUTABLE)

            cancelPendingIntent.send()
        } catch (e: Exception) {
            Log.e("SetupClockViewModel", "Error cancelling alarm", e)
        }
    }

    fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}