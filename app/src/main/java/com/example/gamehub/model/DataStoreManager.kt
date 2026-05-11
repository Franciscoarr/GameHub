package com.example.gamehub.model

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "settings")

class DataStoreManager(private val context: Context) {
    private val USERNAME = stringPreferencesKey("username")
    private val THEME = stringPreferencesKey("theme")

    val settingsFlow: Flow<UserSettings> = context.dataStore.data.map { preferences ->
        UserSettings(
            username = preferences[USERNAME] ?: "Invitado",
            theme = AppTheme.valueOf(preferences[THEME] ?: AppTheme.SYSTEM.name)
        )
    }

    suspend fun saveSettings(settings: UserSettings) {
        context.dataStore.edit { preferences ->
            preferences[USERNAME] = settings.username
            preferences[THEME] = settings.theme.name
        }
    }
}
