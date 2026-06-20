package com.smartcart_merchant.core.storage

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
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
        private val REMEMBER_ME_KEY = booleanPreferencesKey("remember_me")
        private val SAVED_USERNAME_KEY = stringPreferencesKey("saved_username")
        private val IS_VERIFIED_KEY = booleanPreferencesKey("is_verified")
        private val COMPANY_NAME_KEY = stringPreferencesKey("company_name")
        private val APPLICATION_ID_KEY = stringPreferencesKey("application_id")
        private val RUC_KEY = stringPreferencesKey("ruc")
        private val STORE_ID_KEY = stringPreferencesKey("store_id")
        private val STOCK_SNAPSHOTS_KEY = stringPreferencesKey("stock_snapshots")
        private val OFFER_DISCOUNTS_KEY = stringPreferencesKey("offer_discounts")
        private val STORE_HOURS_KEY = stringPreferencesKey("store_hours_override")
    }

    val authToken: Flow<String?> = context.dataStore.data.map { it[TOKEN_KEY] }
    val merchantId: Flow<String?> = context.dataStore.data.map { it[MERCHANT_ID_KEY] }
    val username: Flow<String?> = context.dataStore.data.map { it[USERNAME_KEY] }
    val rememberMe: Flow<Boolean> = context.dataStore.data.map { it[REMEMBER_ME_KEY] ?: false }
    val savedUsername: Flow<String?> = context.dataStore.data.map { it[SAVED_USERNAME_KEY] }
    val isVerified: Flow<Boolean> = context.dataStore.data.map { it[IS_VERIFIED_KEY] ?: false }
    val companyName: Flow<String?> = context.dataStore.data.map { it[COMPANY_NAME_KEY] }
    val applicationId: Flow<String?> = context.dataStore.data.map { it[APPLICATION_ID_KEY] }
    val ruc: Flow<String?> = context.dataStore.data.map { it[RUC_KEY] }
    val storeId: Flow<String?> = context.dataStore.data.map { it[STORE_ID_KEY] }
    val stockSnapshots: Flow<String?> = context.dataStore.data.map { it[STOCK_SNAPSHOTS_KEY] }
    val offerDiscounts: Flow<String?> = context.dataStore.data.map { it[OFFER_DISCOUNTS_KEY] }
    val storeHoursOverride: Flow<String?> = context.dataStore.data.map { it[STORE_HOURS_KEY] }

    suspend fun saveSession(token: String, merchantId: String, username: String) {
        context.dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
            preferences[MERCHANT_ID_KEY] = merchantId
            preferences[USERNAME_KEY] = username
        }
    }

    suspend fun saveRememberMe(enabled: Boolean, username: String?) {
        context.dataStore.edit { preferences ->
            preferences[REMEMBER_ME_KEY] = enabled
            if (enabled && !username.isNullOrBlank()) {
                preferences[SAVED_USERNAME_KEY] = username
            } else if (!enabled) {
                preferences.remove(SAVED_USERNAME_KEY)
            }
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

    suspend fun saveStoreId(storeId: String) {
        context.dataStore.edit { preferences ->
            preferences[STORE_ID_KEY] = storeId
        }
    }

    suspend fun saveStockSnapshots(json: String) {
        context.dataStore.edit { preferences ->
            preferences[STOCK_SNAPSHOTS_KEY] = json
        }
    }

    suspend fun saveOfferDiscounts(json: String) {
        context.dataStore.edit { preferences ->
            preferences[OFFER_DISCOUNTS_KEY] = json
        }
    }

    suspend fun saveStoreHoursOverride(json: String) {
        context.dataStore.edit { preferences ->
            preferences[STORE_HOURS_KEY] = json
        }
    }

    suspend fun clearSession() {
        val remember = context.dataStore.data.first()[REMEMBER_ME_KEY] ?: false
        val savedUser = context.dataStore.data.first()[SAVED_USERNAME_KEY]
        context.dataStore.edit { it.clear() }
        if (remember && !savedUser.isNullOrBlank()) {
            context.dataStore.edit { preferences ->
                preferences[REMEMBER_ME_KEY] = true
                preferences[SAVED_USERNAME_KEY] = savedUser
            }
        }
    }
}
