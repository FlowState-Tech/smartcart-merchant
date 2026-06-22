package com.smartcart_merchant.features.verification.di

import com.smartcart_merchant.features.verification.data.repository.VerificationRepositoryImpl
import com.smartcart_merchant.features.verification.domain.repository.VerificationRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class VerificationModule {

    @Binds
    @Singleton
    abstract fun bindVerificationRepository(impl: VerificationRepositoryImpl): VerificationRepository
}
