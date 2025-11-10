package com.farouktouil.farouktouil.news_feature.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.farouktouil.farouktouil.core.data.local.AppDatabase
import com.farouktouil.farouktouil.core.util.NetworkUtils
import com.farouktouil.farouktouil.news_feature.data.local.entity.NewsEntity
import com.farouktouil.farouktouil.news_feature.data.remote.NewsApiService
import com.farouktouil.farouktouil.news_feature.data.remote.NewsRemoteMediator
import com.farouktouil.farouktouil.news_feature.domain.model.NewsArticle
import com.farouktouil.farouktouil.news_feature.domain.model.NewsSearchQuery
import com.farouktouil.farouktouil.news_feature.domain.repository.NewsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class NewsRepositoryImpl @Inject constructor(
    private val appDatabase: AppDatabase,
    private val apiService: NewsApiService,
    private val networkUtils: NetworkUtils
) : NewsRepository {

    private val newsDao get() = appDatabase.newsDao()

    @OptIn(ExperimentalPagingApi::class)
    override fun getNews(searchQuery: NewsSearchQuery): Flow<PagingData<NewsArticle>> {
        val keyword = searchQuery.keyword.trim()
        val normalizedQuery = keyword
        val pageSize = 10

        val pager = Pager(
            config = PagingConfig(
                pageSize = pageSize,
                enablePlaceholders = false,
                initialLoadSize = pageSize,
                prefetchDistance = pageSize / 2
            ),
            remoteMediator = NewsRemoteMediator(
                appDatabase = appDatabase,
                apiService = apiService,
                searchQuery = searchQuery,
                networkUtils = networkUtils
            ),
            pagingSourceFactory = {
                newsDao.pagingSource(normalizedQuery)
            }
        )

        return pager.flow.map { pagingData ->
            pagingData.map(NewsEntity::toDomain)
        }
    }
}
