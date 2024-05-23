package dev.sangui.wakeupright.alarm

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.RingtoneManager
import android.net.Uri
import android.util.Log

class RingToneProvider {

    private var ringtone: android.media.Ringtone? = null

    fun playRingtone(context: Context) {
        val notification: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        ringtone = RingtoneManager.getRingtone(context, notification)

        // Check if the ringtone is null
        if (ringtone == null) {
            Log.e("MainActivity", "Ringtone is null")
            return
        }

        // Set the audio attributes to use the alarm stream
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ALARM)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        ringtone?.audioAttributes = audioAttributes

        // Check the alarm volume
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val volume = audioManager.getStreamVolume(AudioManager.STREAM_ALARM)
        if (volume == 0) {
            Log.e("MainActivity", "Alarm volume is set to zero")
            return
        }

        // Play the ringtone
        ringtone?.play()
    }

    fun stopRingtone() {
        ringtone?.stop()
        ringtone = null
    }
}