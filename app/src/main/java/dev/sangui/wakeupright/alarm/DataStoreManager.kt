package dev.sangui.wakeupright.alarm

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

import dev.sangui.wakeupright.Constants

private val Context.dataStore by preferencesDataStore(Constants.DATASTORE_SETTINGS_NAME)

class DataStoreManager(private val context: Context) {

    companion object {
        private const val DEFAULT_SELECTED_NUMBER = 0
    }

    fun selectedNumberFlow(id: String): Flow<Int> {
        val key = intPreferencesKey(Constants.DATASTORE_KEY_SELECTED_NUMBER_PREFIX + id)
        return context.dataStore.data
            .map { preferences ->
                preferences[key] ?: DEFAULT_SELECTED_NUMBER
            }
    }

    suspend fun saveSelectedNumber(id: String, number: Int) {
        val key = intPreferencesKey(Constants.DATASTORE_KEY_SELECTED_NUMBER_PREFIX + id)
        context.dataStore.edit { preferences ->
            preferences[key] = number
        }
    }

    fun selectedRingtoneFlow(): Flow<String?> {
        val key = stringPreferencesKey(Constants.DATASTORE_KEY_SELECTED_RINGTONE)
        return context.dataStore.data
            .map { preferences ->
                preferences[key]
            }
    }

    suspend fun saveSelectedRingtone(uri: String) {
        val key = stringPreferencesKey(Constants.DATASTORE_KEY_SELECTED_RINGTONE)
        context.dataStore.edit { preferences ->
            preferences[key] = uri
        }
    }

    fun scheduledAlarmTimeFlow(): Flow<Long?> {
        val key = androidx.datastore.preferences.core.longPreferencesKey(Constants.DATASTORE_KEY_SCHEDULED_ALARM_TIME)
        return context.dataStore.data
            .map {
                it[key]
            }
    }

    suspend fun saveScheduledAlarmTime(time: Long) {
        val key = androidx.datastore.preferences.core.longPreferencesKey(Constants.DATASTORE_KEY_SCHEDULED_ALARM_TIME)
        context.dataStore.edit {
            it[key] = time
        }
    }

    suspend fun clearScheduledAlarmTime() {
        val key = androidx.datastore.preferences.core.longPreferencesKey(Constants.DATASTORE_KEY_SCHEDULED_ALARM_TIME)
        context.dataStore.edit {
            it.remove(key)
        }
    }

    fun notificationIdFlow(): Flow<Int?> {
        val key = intPreferencesKey(Constants.DATASTORE_KEY_NOTIFICATION_ID)
        return context.dataStore.data
            .map {
                it[key]
            }
    }

    suspend fun saveNotificationId(id: Int) {
        val key = intPreferencesKey(Constants.DATASTORE_KEY_NOTIFICATION_ID)
        context.dataStore.edit {
            it[key] = id
        }
    }

    suspend fun clearNotificationId() {
        val key = intPreferencesKey(Constants.DATASTORE_KEY_NOTIFICATION_ID)
        context.dataStore.edit {
            it.remove(key)
        }
    }
}