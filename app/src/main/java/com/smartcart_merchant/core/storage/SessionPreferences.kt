package com.smartcart_merchant.core.storage

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "smartcart_session")

@Singleton
class SessionPreferences @Inject constructor(private val context: Context) {

    companion object {
        private val TOKEN_KEY = stringPreferencesKey("auth_token")
        private val MERCHANT_ID_KEY = stringPreferencesKey("merchant_id")
        private val USERNAME_KEY = stringPreferencesKey("username")
    }

    val authToken: Flow<String?> = context.dataStore.data.map { it[TOKEN_KEY] }
    val merchantId: Flow<String?> = context.dataStore.data.map { it[MERCHANT_ID_KEY] }

    suspend fun saveSession(token: String, merchantId: String, username: String) {
        context.dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
            preferences[MERCHANT_ID_KEY] = merchantId
            preferences[USERNAME_KEY] = username
        }
    }

    suspend fun clearSession() {
        context.dataStore.edit { it.clear() }
    }
}