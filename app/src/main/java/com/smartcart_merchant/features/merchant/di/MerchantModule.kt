package com.smartcart_merchant.features.merchant.di

import com.smartcart_merchant.features.merchant.data.remote.api.MerchantApi
import com.smartcart_merchant.features.merchant.data.repository.MerchantRepositoryImpl
import com.smartcart_merchant.features.merchant.domain.repository.MerchantRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MerchantModule {

    @Provides
    @Singleton
    fun provideMerchantApi(retrofit: Retrofit): MerchantApi {
        return retrofit.create(MerchantApi::class.java)
    }

    @Provides
    @Singleton
    fun provideMerchantRepository(impl: MerchantRepositoryImpl): MerchantRepository = impl
}
