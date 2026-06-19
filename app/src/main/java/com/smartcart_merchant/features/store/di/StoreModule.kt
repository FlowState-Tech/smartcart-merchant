package com.smartcart_merchant.features.store.di

import com.smartcart_merchant.features.store.data.remote.api.StoreApi
import com.smartcart_merchant.features.store.data.repository.StoreRepositoryImpl
import com.smartcart_merchant.features.store.domain.repository.StoreRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object StoreModule {

    @Provides
    @Singleton
    fun provideStoreApi(retrofit: Retrofit): StoreApi {
        return retrofit.create(StoreApi::class.java)
    }

    @Provides
    @Singleton
    fun provideStoreRepository(api: StoreApi): StoreRepository {
        return StoreRepositoryImpl(api)
    }
}