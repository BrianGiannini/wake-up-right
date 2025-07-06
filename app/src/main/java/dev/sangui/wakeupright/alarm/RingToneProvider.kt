package dev.sangui.wakeupright.alarm

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.RingtoneManager
import android.net.Uri

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject
import androidx.core.net.toUri

class RingToneProvider {
    private var ringtone: android.media.Ringtone? = null
    private val dataStoreManager: DataStoreManager by inject(DataStoreManager::class.java)

    fun playRingtone(context: Context) {
        CoroutineScope(Dispatchers.Main).launch {
            val ringtoneUriString = dataStoreManager.selectedRingtoneFlow().first()

            val ringtoneUri: Uri = ringtoneUriString?.toUri() ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)

            ringtone = RingtoneManager.getRingtone(context, ringtoneUri)

            // Check if the ringtone is null
            if (ringtone == null) {
                return@launch
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
                return@launch
            }

            // Play the ringtone
            ringtone?.play()


        }
    }

    fun stopRingtone() {
        ringtone?.stop()
        ringtone = null
    }
}