package com.smartcart_merchant.features.store.di

import com.smartcart_merchant.features.store.data.remote.api.NominatimApi
import com.smartcart_merchant.features.store.data.remote.api.StoreApi
import com.smartcart_merchant.features.store.data.repository.ReverseGeocodingRepositoryImpl
import com.smartcart_merchant.features.store.data.repository.StoreRepositoryImpl
import com.smartcart_merchant.features.store.domain.repository.ReverseGeocodingRepository
import com.smartcart_merchant.features.store.domain.repository.StoreRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
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

    @Provides
    @Singleton
    @Named("nominatim")
    fun provideNominatimOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .header("User-Agent", "SmartCartMerchant/1.0")
                    .build()
                chain.proceed(request)
            }
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    @Named("nominatim")
    fun provideNominatimRetrofit(@Named("nominatim") client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://nominatim.openstreetmap.org/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideNominatimApi(@Named("nominatim") retrofit: Retrofit): NominatimApi {
        return retrofit.create(NominatimApi::class.java)
    }

    @Provides
    @Singleton
    fun provideReverseGeocodingRepository(api: NominatimApi): ReverseGeocodingRepository {
        return ReverseGeocodingRepositoryImpl(api)
    }
}