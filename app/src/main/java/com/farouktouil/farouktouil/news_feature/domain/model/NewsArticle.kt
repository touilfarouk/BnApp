package com.farouktouil.farouktouil.news_feature.domain.model

data class NewsArticle(
    val id: Int,
    val rubrique: String,
    val title: String,
    val titleAr: String,
    val publishedDate: String,
    val pictureUrl: String?
)
