package com.smartcart_merchant.features.auth.domain.usecase

import com.smartcart_merchant.core.common.Resource
import com.smartcart_merchant.features.auth.domain.model.Session
import com.smartcart_merchant.features.auth.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SignUpUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    operator fun invoke(username: String, password: String): Flow<Resource<Session>> = flow {
        emit(Resource.Loading())
        val result = repository.signUp(username, password)
        emit(result)
    }
}