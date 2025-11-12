package com.farouktouil.farouktouil.core.di

import com.farouktouil.farouktouil.consultation_feature.data.remote.ConsultationApiService
import com.farouktouil.farouktouil.core.di.ConsultationApi
import com.farouktouil.farouktouil.core.di.NewsApi
import com.farouktouil.farouktouil.core.di.PersonnelApi
import com.farouktouil.farouktouil.news_feature.data.remote.NewsApiService
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
    @PersonnelApi
    fun providePersonnelOkHttpClient(): OkHttpClient {
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
    @PersonnelApi
    fun providePersonnelRetrofit(@PersonnelApi okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://bneder.dz/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    @ConsultationApi
    fun provideConsultationOkHttpClient(): OkHttpClient {
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
    @ConsultationApi
    fun provideConsultationRetrofit(@ConsultationApi okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://bneder.dz/api/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    @NewsApi
    fun provideNewsOkHttpClient(): OkHttpClient {
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
    @NewsApi
    fun provideNewsRetrofit(@NewsApi okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://bneder.dz/api/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun providePersonnelApiService(@PersonnelApi personnelRetrofit: Retrofit): PersonnelApiService {
        return personnelRetrofit.create(PersonnelApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideConsultationApiService(@ConsultationApi consultationRetrofit: Retrofit): ConsultationApiService {
        return consultationRetrofit.create(ConsultationApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideNewsApiService(@NewsApi newsRetrofit: Retrofit): NewsApiService {
        return newsRetrofit.create(NewsApiService::class.java)
    }
}