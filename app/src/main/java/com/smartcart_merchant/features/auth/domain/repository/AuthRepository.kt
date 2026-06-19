package com.smartcart_merchant.features.auth.domain.repository

import com.smartcart_merchant.core.common.Resource
import com.smartcart_merchant.features.auth.domain.model.Session

interface AuthRepository {
    suspend fun signIn(username: String, password: String): Resource<Session>
    suspend fun signUp(username: String, password: String): Resource<Session>
}
