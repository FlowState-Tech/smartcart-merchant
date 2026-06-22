package com.smartcart_merchant.features.auth.data.remote.api

import com.smartcart_merchant.features.auth.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("authentication/sign-in")
    suspend fun signIn(@Body request: SignInRequestDto): Response<SignInResponseDto>

    @POST("authentication/sign-up")
    suspend fun signUp(@Body request: SignUpRequestDto): Response<SignUpResponseDto>
}