package com.smartcart_merchant.features.auth.data.repository

import android.util.Log
import com.smartcart_merchant.core.common.Resource
import com.smartcart_merchant.features.auth.data.remote.api.AuthApi
import com.smartcart_merchant.features.auth.data.remote.dto.SignInRequestDto
import com.smartcart_merchant.features.auth.data.remote.dto.SignUpRequestDto
import com.smartcart_merchant.features.auth.domain.model.Session
import com.smartcart_merchant.features.auth.domain.repository.AuthRepository
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "AuthRepositoryImpl"

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val api: AuthApi
) : AuthRepository {
    override suspend fun signIn(username: String, password: String): Resource<Session> {
        return try {
            Log.d(TAG, "Attempting sign-in for user: $username")
            val response = api.signIn(SignInRequestDto(username, password))
            Log.d(TAG, "Response received - code: ${response.code()}, isSuccessful: ${response.isSuccessful}")
            
            if (response.isSuccessful) {
                val body = response.body()
                Log.d(TAG, "Response body: $body")
                if (body != null) {
                    val session = Session(
                        id = body.id,
                        username = body.username,
                        token = body.token,
                        roles = listOf("ROLE_MERCHANT")
                    )
                    Log.d(TAG, "Sign-in successful for user: ${session.username}")
                    Resource.Success(session)
                } else {
                    Log.e(TAG, "Empty response body from server")
                    Resource.Error("Empty response from server")
                }
            } else {
                val errorMsg = "Sign-in failed: ${response.code()} ${response.message()}"
                Log.e(TAG, errorMsg)
                Resource.Error(errorMsg)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Sign-in exception: ${e.message}", e)
            Resource.Error(e.localizedMessage ?: "Unknown error: ${e.message}")
        }
    }

    override suspend fun signUp(username: String, password: String): Resource<Session> {
        return try {
            Log.d(TAG, "Attempting sign-up for user: $username")
            val response = api.signUp(SignUpRequestDto(username, password))
            Log.d(TAG, "Response received - code: ${response.code()}, isSuccessful: ${response.isSuccessful}")
            
            if (response.isSuccessful) {
                val body = response.body()
                Log.d(TAG, "Response body: $body")
                if (body != null) {
                    val session = Session(
                        id = body.id,
                        username = body.username,
                        token = "",
                        roles = listOf("ROLE_MERCHANT")
                    )
                    Log.d(TAG, "Sign-up successful for user: ${session.username}")
                    Resource.Success(session)
                } else {
                    Log.e(TAG, "Empty response body from server")
                    Resource.Error("Empty response from server")
                }
            } else {
                val errorMsg = "Sign-up failed: ${response.code()} ${response.message()}"
                Log.e(TAG, errorMsg)
                Resource.Error(errorMsg)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Sign-up exception: ${e.message}", e)
            Resource.Error(e.localizedMessage ?: "Unknown error: ${e.message}")
        }
    }
}
