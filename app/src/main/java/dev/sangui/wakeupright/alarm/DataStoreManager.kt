package dev.sangui.wakeupright.alarm

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("settings")

class DataStoreManager(private val context: Context) {

    companion object {
        private const val DEFAULT_SELECTED_NUMBER = 0
    }

    fun selectedNumberFlow(id: String): Flow<Int> {
        val key = intPreferencesKey("selected_number_$id")
        return context.dataStore.data
            .map { preferences ->
                preferences[key] ?: DEFAULT_SELECTED_NUMBER
            }
    }

    suspend fun saveSelectedNumber(id: String, number: Int) {
        val key = intPreferencesKey("selected_number_$id")
        context.dataStore.edit { preferences ->
            preferences[key] = number
        }
    }

    fun selectedRingtoneFlow(): Flow<String?> {
        val key = stringPreferencesKey("selected_ringtone")
        return context.dataStore.data
            .map { preferences ->
                preferences[key]
            }
    }

    suspend fun saveSelectedRingtone(uri: String) {
        val key = stringPreferencesKey("selected_ringtone")
        context.dataStore.edit { preferences ->
            preferences[key] = uri
        }
    }
}