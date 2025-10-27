package com.farouktouil.farouktouil.core.di

import com.farouktouil.farouktouil.deliverer_feature.data.remote.DelivererApiService
import com.farouktouil.farouktouil.personnel_feature.data.remote.PersonnelApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    @DelivererApi
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                }
            )
            .build()
    }

    @Provides
    @Singleton
    @DelivererApi
    fun provideRetrofit(@DelivererApi okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://192.168.100.3/php_api/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    @PersonnelApi
    fun providePersonnelRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://bneder.dz/")
            .client(OkHttpClient.Builder()
                .addInterceptor(
                    HttpLoggingInterceptor().apply {
                        level = HttpLoggingInterceptor.Level.BODY
                    }
                )
                .build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideDelivererApiService(@DelivererApi retrofit: Retrofit): DelivererApiService {
        return retrofit.create(DelivererApiService::class.java)
    }

    @Provides
    @Singleton
    fun providePersonnelApiService(@PersonnelApi personnelRetrofit: Retrofit): PersonnelApiService {
        return personnelRetrofit.create(PersonnelApiService::class.java)
    }
}