package com.farouktouil.farouktouil.news_feature.domain.use_case

import androidx.paging.PagingData
import com.farouktouil.farouktouil.news_feature.domain.model.NewsArticle
import com.farouktouil.farouktouil.news_feature.domain.model.NewsSearchQuery
import com.farouktouil.farouktouil.news_feature.domain.repository.NewsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetNewsUseCase @Inject constructor(
    private val repository: NewsRepository
) {
    operator fun invoke(searchQuery: NewsSearchQuery): Flow<PagingData<NewsArticle>> {
        return repository.getNews(searchQuery)
    }
}
