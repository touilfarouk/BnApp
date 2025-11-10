package com.farouktouil.farouktouil.news_feature.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.farouktouil.farouktouil.news_feature.data.local.entity.NewsEntity

@Dao
interface NewsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(news: List<NewsEntity>)

    @Query(
        "SELECT * FROM news_articles " +
            "WHERE (:query = '' OR title LIKE '%' || :query || '%' " +
            "OR rubrique LIKE '%' || :query || '%' " +
            "OR title_ar LIKE '%' || :query || '%') " +
            "ORDER BY published_date DESC, id DESC"
    )
    fun pagingSource(query: String): PagingSource<Int, NewsEntity>

    @Query("DELETE FROM news_articles")
    suspend fun clearAll()

    @Query("SELECT COUNT(*) FROM news_articles")
    suspend fun getCount(): Int

    @Transaction
    @Query("SELECT * FROM news_articles ORDER BY published_date DESC, id DESC")
    suspend fun getAll(): List<NewsEntity>
}
