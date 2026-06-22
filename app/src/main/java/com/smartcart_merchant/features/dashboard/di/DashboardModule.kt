package com.smartcart_merchant.features.dashboard.di

import com.smartcart_merchant.features.dashboard.data.remote.api.DashboardApi
import com.smartcart_merchant.features.dashboard.data.repository.DashboardRepositoryImpl
import com.smartcart_merchant.features.dashboard.domain.repository.DashboardRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DashboardModule {

    @Provides
    @Singleton
    fun provideDashboardApi(retrofit: Retrofit): DashboardApi {
        return retrofit.create(DashboardApi::class.java)
    }

    @Provides
    @Singleton
    fun provideDashboardRepository(impl: DashboardRepositoryImpl): DashboardRepository = impl
}
