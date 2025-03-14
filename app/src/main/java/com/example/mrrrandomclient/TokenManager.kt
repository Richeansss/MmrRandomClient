package com.example.mrrrandomclient.storage

import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.runBlocking

private val Context.dataStore by preferencesDataStore(name = "user_prefs")

class TokenManager(private val context: Context) {

    companion object {
        private val TOKEN_KEY = stringPreferencesKey("token")
        private val PLAYER_NAME_KEY = stringPreferencesKey("player_name")
    }

    val token: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[TOKEN_KEY]
    }

    val playerName: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[PLAYER_NAME_KEY]
    }

    suspend fun saveToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
        }
    }

    suspend fun savePlayerName(name: String) {
        context.dataStore.edit { preferences ->
            preferences[PLAYER_NAME_KEY] = name
        }
    }

    suspend fun clearData() {
        context.dataStore.edit { preferences ->
            preferences.remove(TOKEN_KEY)
            preferences.remove(PLAYER_NAME_KEY)
        }
    }

    // Получение токена и имени при запуске (синхронно)
    fun getStoredToken(): String? {
        return runBlocking { context.dataStore.data.first()[TOKEN_KEY] }
    }

    fun getStoredPlayerName(): String? {
        return runBlocking { context.dataStore.data.first()[PLAYER_NAME_KEY] }
    }
}
