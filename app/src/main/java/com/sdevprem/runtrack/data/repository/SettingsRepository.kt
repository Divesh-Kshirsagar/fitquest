package com.sdevprem.runtrack.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

    companion object {
        val IS_DARK_MODE = booleanPreferencesKey("is_dark_mode")
        val UNIT = stringPreferencesKey("unit") // "km" or "mi"
        val ARE_NOTIFICATIONS_ENABLED = booleanPreferencesKey("are_notifications_enabled")
    }

    val isDarkMode = dataStore.data.map { it[IS_DARK_MODE] ?: false }
    val unit = dataStore.data.map { it[UNIT] ?: "km" }
    val areNotificationsEnabled = dataStore.data.map { it[ARE_NOTIFICATIONS_ENABLED] ?: true }

    suspend fun setDarkMode(enabled: Boolean) = dataStore.edit { it[IS_DARK_MODE] = enabled }
    suspend fun setUnit(unit: String) = dataStore.edit { it[UNIT] = unit }
    suspend fun setNotificationsEnabled(enabled: Boolean) = dataStore.edit { it[ARE_NOTIFICATIONS_ENABLED] = enabled }
}
