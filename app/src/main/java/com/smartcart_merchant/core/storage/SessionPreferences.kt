package com.smartcart_merchant.core.storage

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
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
        private val IS_VERIFIED_KEY = booleanPreferencesKey("is_verified")
        private val COMPANY_NAME_KEY = stringPreferencesKey("company_name")
        private val APPLICATION_ID_KEY = stringPreferencesKey("application_id")
        private val RUC_KEY = stringPreferencesKey("ruc")
    }

    val authToken: Flow<String?> = context.dataStore.data.map { it[TOKEN_KEY] }
    val merchantId: Flow<String?> = context.dataStore.data.map { it[MERCHANT_ID_KEY] }
    val isVerified: Flow<Boolean> = context.dataStore.data.map { it[IS_VERIFIED_KEY] ?: false }
    val companyName: Flow<String?> = context.dataStore.data.map { it[COMPANY_NAME_KEY] }
    val applicationId: Flow<String?> = context.dataStore.data.map { it[APPLICATION_ID_KEY] }
    val ruc: Flow<String?> = context.dataStore.data.map { it[RUC_KEY] }

    suspend fun saveSession(token: String, merchantId: String, username: String) {
        context.dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
            preferences[MERCHANT_ID_KEY] = merchantId
            preferences[USERNAME_KEY] = username
        }
    }

    suspend fun saveVerificationStatus(applicationId: String, companyName: String, ruc: String) {
        context.dataStore.edit { preferences ->
            preferences[IS_VERIFIED_KEY] = true
            preferences[COMPANY_NAME_KEY] = companyName
            preferences[APPLICATION_ID_KEY] = applicationId
            preferences[RUC_KEY] = ruc
        }
    }

    suspend fun clearSession() {
        context.dataStore.edit { it.clear() }
    }
}