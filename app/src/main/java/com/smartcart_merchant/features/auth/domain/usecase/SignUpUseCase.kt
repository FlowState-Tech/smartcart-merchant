package com.smartcart_merchant.features.auth.domain.usecase

import com.smartcart_merchant.core.common.Resource
import com.smartcart_merchant.core.storage.SessionPreferences
import com.smartcart_merchant.features.auth.domain.model.Session
import com.smartcart_merchant.features.auth.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SignUpUseCase @Inject constructor(
    private val repository: AuthRepository,
    private val sessionPreferences: SessionPreferences
) {
    operator fun invoke(username: String, password: String): Flow<Resource<Session>> = flow {
        emit(Resource.Loading())

        val signUpResult = repository.signUp(username, password)
        if (signUpResult !is Resource.Success || signUpResult.data == null) {
            emit(Resource.Error(signUpResult.message ?: "Error al registrarse"))
            return@flow
        }

        val signInResult = repository.signIn(username, password)
        if (signInResult is Resource.Success && signInResult.data != null) {
            val session = signInResult.data
            if (session.roles.contains("ROLE_MERCHANT")) {
                sessionPreferences.saveSession(
                    token = session.token,
                    merchantId = session.id,
                    username = session.username
                )
                emit(Resource.Success(session))
            } else {
                emit(Resource.Error("Acceso denegado: No tienes el rol de Comerciante."))
            }
        } else {
            emit(Resource.Error(signInResult.message ?: "Registro exitoso, pero no se pudo iniciar sesión automáticamente"))
        }
    }
}