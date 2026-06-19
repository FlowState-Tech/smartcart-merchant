package com.smartcart_merchant.features.auth.data.remote.dto

import com.google.gson.annotations.SerializedName

data class SignInRequestDto(
    @SerializedName("username") val username: String, 
    @SerializedName("password") val password: String
)

data class SignInResponseDto(
    @SerializedName("id") val id: String,
    @SerializedName("username") val username: String,
    @SerializedName("token") val token: String,
)

data class SignUpRequestDto(
    @SerializedName("username") val username: String,
    @SerializedName("password") val password: String,
    @SerializedName("roles") val roles: List<String> = listOf("ROLE_MERCHANT")
)

data class SignUpResponseDto(
    @SerializedName("id") val id: String, 
    @SerializedName("username") val username: String
)