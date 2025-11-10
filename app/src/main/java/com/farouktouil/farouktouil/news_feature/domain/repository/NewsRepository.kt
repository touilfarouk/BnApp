package com.farouktouil.farouktouil.news_feature.domain.repository

import androidx.paging.PagingData
import com.farouktouil.farouktouil.news_feature.domain.model.NewsArticle
import com.farouktouil.farouktouil.news_feature.domain.model.NewsSearchQuery
import kotlinx.coroutines.flow.Flow

interface NewsRepository {
    fun getNews(searchQuery: NewsSearchQuery): Flow<PagingData<NewsArticle>>
}
