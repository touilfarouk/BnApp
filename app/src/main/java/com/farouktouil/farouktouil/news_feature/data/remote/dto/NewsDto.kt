package com.farouktouil.farouktouil.news_feature.data.remote.dto

import com.farouktouil.farouktouil.news_feature.data.local.entity.NewsEntity
import com.google.gson.annotations.SerializedName

/**
 * DTO representation of a news article returned by the remote API.
 */
data class NewsDto(
    @SerializedName("rubrique")
    val rubrique: String?,
    @SerializedName("titre")
    val title: String?,
    @SerializedName("titre_ar")
    val titleAr: String?,
    @SerializedName("date")
    val date: String?,
    @SerializedName("cle")
    val id: Int,
    @SerializedName("pic_path")
    val pictureUrl: String?
) {
    fun toEntity(): NewsEntity {
        return NewsEntity(
            id = id,
            rubrique = rubrique?.trim().orEmpty(),
            title = title?.trim().orEmpty(),
            titleAr = titleAr?.trim().orEmpty(),
            publishedDate = date?.trim().orEmpty(),
            pictureUrl = pictureUrl?.trim()?.takeIf { it.isNotEmpty() },
            lastUpdated = System.currentTimeMillis()
        )
    }
}

/**
 * Wrapper around the paginated news response from the API. The backend returns both `newsList` and
 * `data`, so we expose a helper [items] list that prefers `newsList` and falls back to `data`.
 */
data class NewsPaginatedResponse<T>(
    @SerializedName("reponse")
    val response: String? = null,
    @SerializedName("newsList")
    val newsList: List<T>? = null,
    @SerializedName("data")
    val data: List<T>? = null,
    @SerializedName("pagination")
    val pagination: NewsPagination? = null
) {
    val items: List<T>
        get() = newsList ?: data ?: emptyList()
}

data class NewsPagination(
    @SerializedName("currentPage")
    val currentPage: Int = 1,
    @SerializedName("pageSize")
    val pageSize: Int = 10,
    @SerializedName("totalItems")
    val totalItems: Int = 0,
    @SerializedName("totalPages")
    val totalPages: Int = 0,
    @SerializedName("hasNext")
    val hasNext: Boolean = false,
    @SerializedName("hasPrevious")
    val hasPrevious: Boolean = false
)
