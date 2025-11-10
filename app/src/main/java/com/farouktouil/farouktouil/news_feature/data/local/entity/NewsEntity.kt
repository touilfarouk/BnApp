package com.farouktouil.farouktouil.news_feature.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.farouktouil.farouktouil.news_feature.domain.model.NewsArticle

@Entity(
    tableName = "news_articles",
    indices = [Index(value = ["title"], unique = false)]
)
data class NewsEntity(
    @PrimaryKey
    val id: Int,
    val rubrique: String,
    val title: String,
    @ColumnInfo(name = "title_ar")
    val titleAr: String,
    @ColumnInfo(name = "published_date")
    val publishedDate: String,
    @ColumnInfo(name = "picture_url")
    val pictureUrl: String?,
    @ColumnInfo(name = "last_updated")
    val lastUpdated: Long
) {
    fun toDomain(): NewsArticle {
        return NewsArticle(
            id = id,
            rubrique = rubrique,
            title = title,
            titleAr = titleAr,
            publishedDate = publishedDate,
            pictureUrl = pictureUrl
        )
    }
}
