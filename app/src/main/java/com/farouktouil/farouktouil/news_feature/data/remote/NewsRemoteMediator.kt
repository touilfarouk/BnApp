package com.farouktouil.farouktouil.news_feature.data.remote

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.farouktouil.farouktouil.core.data.local.AppDatabase
import com.farouktouil.farouktouil.core.util.NetworkUtils
import com.farouktouil.farouktouil.news_feature.data.local.entity.NewsEntity
import com.farouktouil.farouktouil.news_feature.data.local.entity.NewsRemoteKey
import com.farouktouil.farouktouil.news_feature.domain.model.NewsSearchQuery
import java.io.IOException
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class NewsRemoteMediator @Inject constructor(
    private val appDatabase: AppDatabase,
    private val apiService: NewsApiService,
    private val searchQuery: NewsSearchQuery,
    private val networkUtils: NetworkUtils
) : RemoteMediator<Int, NewsEntity>() {

    private val newsDao = appDatabase.newsDao()
    private val remoteKeysDao = appDatabase.newsRemoteKeysDao()
    private val currentQuery: String?
        get() = searchQuery.keyword.trim().takeIf { it.isNotEmpty() }

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, NewsEntity>
    ): MediatorResult {
        val query = currentQuery

        if (loadType == LoadType.REFRESH && !networkUtils.isNetworkAvailable()) {
            val cachedCount = newsDao.getCount()
            if (cachedCount == 0) {
                return MediatorResult.Error(IOException("Aucune connexion Internet"))
            }
            return MediatorResult.Success(endOfPaginationReached = true)
        }

        val page = when (loadType) {
            LoadType.REFRESH -> 1
            LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
            LoadType.APPEND -> {
                val remoteKey = getRemoteKeyForLastItem(state)
                remoteKey?.nextKey ?: return MediatorResult.Success(endOfPaginationReached = remoteKey != null)
            }
        }

        try {
            val response = apiService.getNews(
                page = page,
                pageSize = state.config.pageSize,
                search = query
            )

            if (!response.isSuccessful) {
                return MediatorResult.Error(IOException("Erreur serveur ${response.code()}"))
            }

            val body = response.body()
                ?: return MediatorResult.Error(NullPointerException("Réponse vide"))

            val newsDtos = body.items
            val endOfPaginationReached = newsDtos.isEmpty()

            appDatabase.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    remoteKeysDao.clearAll()
                    newsDao.clearAll()
                }

                if (newsDtos.isNotEmpty()) {
                    val entities = newsDtos.map { it.toEntity() }
                    val prevKey = if (page == 1) null else page - 1
                    val nextKey = if (endOfPaginationReached) null else page + 1
                    val keys = entities.map { entity ->
                        NewsRemoteKey(
                            newsId = entity.id,
                            prevKey = prevKey,
                            nextKey = nextKey,
                            query = query
                        )
                    }
                    remoteKeysDao.insertAll(keys)
                    newsDao.insertAll(entities)
                }
            }

            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (exception: Exception) {
            return MediatorResult.Error(exception)
        }
    }

    private suspend fun getRemoteKeyForLastItem(
        state: PagingState<Int, NewsEntity>
    ): NewsRemoteKey? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }
            ?.data?.lastOrNull()
            ?.let { entity -> remoteKeysDao.remoteKeyByNewsId(entity.id, currentQuery) }
            ?: remoteKeysDao.lastKey(currentQuery)
    }
}
