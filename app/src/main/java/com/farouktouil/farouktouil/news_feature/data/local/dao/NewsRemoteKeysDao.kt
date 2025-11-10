package com.farouktouil.farouktouil.news_feature.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.farouktouil.farouktouil.news_feature.data.local.entity.NewsRemoteKey

@Dao
interface NewsRemoteKeysDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(keys: List<NewsRemoteKey>)

    @Query("SELECT * FROM news_remote_keys WHERE newsId = :newsId AND (query IS :query OR query = :query) LIMIT 1")
    suspend fun remoteKeyByNewsId(newsId: Int, query: String?): NewsRemoteKey?

    @Query("DELETE FROM news_remote_keys")
    suspend fun clearAll()

    @Query("SELECT * FROM news_remote_keys WHERE query IS :query OR query = :query ORDER BY id DESC LIMIT 1")
    suspend fun lastKey(query: String?): NewsRemoteKey?
}
