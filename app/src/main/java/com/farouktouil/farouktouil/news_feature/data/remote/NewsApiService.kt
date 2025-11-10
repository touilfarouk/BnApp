package com.farouktouil.farouktouil.news_feature.data.remote

import com.farouktouil.farouktouil.news_feature.data.remote.dto.NewsDto
import com.farouktouil.farouktouil.news_feature.data.remote.dto.NewsPaginatedResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApiService {
    @GET("news/news-paginated.php")
    suspend fun getNews(
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int,
        @Query("search") search: String? = null
    ): Response<NewsPaginatedResponse<NewsDto>>
}
