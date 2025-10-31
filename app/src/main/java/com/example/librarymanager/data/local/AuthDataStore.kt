package com.example.librarymanager.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.authDataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_preferences")

@Singleton
class AuthDataStore @Inject constructor(
    @ApplicationContext context: Context
) {
    private val dataStore = context.authDataStore

    private object PreferencesKeys {
        val ACCESS_TOKEN = stringPreferencesKey("access_token")
        val ID_TOKEN = stringPreferencesKey("id_token")
        val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
    }

    val authTokens: Flow<AuthTokens> = dataStore.data
        .map { preferences ->
            AuthTokens(
                accessToken = preferences[PreferencesKeys.ACCESS_TOKEN] ?: "",
                idToken = preferences[PreferencesKeys.ID_TOKEN] ?: "",
                refreshToken = preferences[PreferencesKeys.REFRESH_TOKEN] ?: ""
            )
        }

    suspend fun saveTokens(
        accessToken: String,
        idToken: String,
        refreshToken: String
    ) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.ACCESS_TOKEN] = accessToken
            preferences[PreferencesKeys.ID_TOKEN] = idToken
            preferences[PreferencesKeys.REFRESH_TOKEN] = refreshToken
        }
    }

    suspend fun clearTokens() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}

data class AuthTokens(
    val accessToken: String = "",
    val idToken: String = "",
    val refreshToken: String = ""
) {
    val isLoggedIn: Boolean
        get() = accessToken.isNotBlank() && idToken.isNotBlank()
}
