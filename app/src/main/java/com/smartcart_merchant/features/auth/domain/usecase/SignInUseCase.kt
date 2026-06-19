package com.smartcart_merchant.features.auth.domain.usecase

import com.smartcart_merchant.core.common.Resource
import com.smartcart_merchant.core.storage.SessionPreferences
import com.smartcart_merchant.features.auth.domain.repository.AuthRepository
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SignInUseCase @Inject constructor(
    private val repository: AuthRepository,
    private val sessionPreferences: SessionPreferences
) {
    operator fun invoke(username: String, password: String) = flow {
        emit(Resource.Loading())

        // Hacemos el login contra el backend
        val result = repository.signIn(username, password)

        if (result is Resource.Success && result.data != null) {
            val session = result.data

            // Validamos que el usuario logueado realmente tenga el rol requerido para usar esta app
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
            emit(Resource.Error(result.message ?: "Error desconocido al iniciar sesión"))
        }
    }
}