package com.smartcart_merchant.features.auth.di

import com.smartcart_merchant.features.auth.data.repository.AuthRepositoryImpl
import com.smartcart_merchant.features.auth.domain.repository.AuthRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthModule {
    @Binds
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository
}
