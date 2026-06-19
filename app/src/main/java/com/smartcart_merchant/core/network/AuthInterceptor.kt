package com.smartcart_merchant.core.network

import com.smartcart_merchant.core.storage.SessionPreferences
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.flow.firstOrNull
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val sessionPreferences: SessionPreferences
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Bloqueamos de forma segura el hilo de red solo para extraer la cadena del flujo síncronamente
        val token = runBlocking {
            try {
                sessionPreferences.authToken.firstOrNull()
            } catch (e: Exception) {
                null
            }
        }

        val requestBuilder = originalRequest.newBuilder()
        if (!token.isNullOrBlank()) {
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }

        return chain.proceed(requestBuilder.build())
    }
}