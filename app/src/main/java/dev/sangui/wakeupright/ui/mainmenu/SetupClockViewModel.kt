package dev.sangui.wakeupright.ui.mainmenu

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import dev.sangui.wakeupright.alarm.AlarmItem
import dev.sangui.wakeupright.alarm.AlarmScheduler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDateTime

class SetupClockViewModel(
    application: Application,
    private val alarmScheduler: AlarmScheduler,
) : AndroidViewModel(application) {

    private var alarmItem: AlarmItem? = null
    private val _scheduledDate = MutableStateFlow<LocalDateTime?>(null)
    val scheduledDate: StateFlow<LocalDateTime?> get() = _scheduledDate.asStateFlow()

    private var vibrator: Vibrator? = null

    init {
        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = getApplication<Application>().getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? android.os.VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            getApplication<Application>().getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

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

    fun cancelAlarm() {
        stopVibration()
        try {
            alarmItem?.let {
                alarmScheduler.cancel(it.hashCode())
            }
            _scheduledDate.value = null
        } catch (e: Exception) {
            Log.e("SetupClockViewModel", "Error cancelling alarm", e)
        }
    }

    fun startVibration() {
        vibrator?.let { v ->
            if (v.hasVibrator()) {  // Check if the hardware has a vibrator
                val pattern = longArrayOf(200, 700, 600)
                val amplitude = intArrayOf(0, 255, 0)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    // New API
                    v.vibrate(VibrationEffect.createWaveform(pattern, amplitude, 1))
                } else {
                    // Deprecated in API 26
                    v.vibrate(pattern, 0)
                }
            }
        }
    }

    fun stopVibration() {
        vibrator?.cancel()
    }

    fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}