package com.farouktouil.farouktouil.news_feature.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "news_remote_keys",
    indices = [Index(value = ["newsId", "query"], unique = true)]
)
data class NewsRemoteKey(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val newsId: Int,
    val prevKey: Int?,
    val nextKey: Int?,
    val query: String?
)
