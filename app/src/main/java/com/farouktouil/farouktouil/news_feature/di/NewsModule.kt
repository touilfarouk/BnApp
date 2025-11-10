package com.farouktouil.farouktouil.news_feature.di

import com.farouktouil.farouktouil.core.data.local.AppDatabase
import com.farouktouil.farouktouil.core.util.NetworkUtils
import com.farouktouil.farouktouil.news_feature.data.remote.NewsApiService
import com.farouktouil.farouktouil.news_feature.data.repository.NewsRepositoryImpl
import com.farouktouil.farouktouil.news_feature.domain.repository.NewsRepository
import com.farouktouil.farouktouil.news_feature.domain.use_case.GetNewsUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NewsModule {

    @Provides
    @Singleton
    fun provideNewsRepository(
        appDatabase: AppDatabase,
        newsApiService: NewsApiService,
        networkUtils: NetworkUtils
    ): NewsRepository {
        return NewsRepositoryImpl(
            appDatabase = appDatabase,
            apiService = newsApiService,
            networkUtils = networkUtils
        )
    }

    @Provides
    @Singleton
    fun provideGetNewsUseCase(newsRepository: NewsRepository): GetNewsUseCase {
        return GetNewsUseCase(newsRepository)
    }
}
