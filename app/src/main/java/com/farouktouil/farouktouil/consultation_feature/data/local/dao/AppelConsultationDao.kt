package com.farouktouil.farouktouil.consultation_feature.data.local.dao

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.farouktouil.farouktouil.consultation_feature.data.local.entity.AppelConsultationEntity

@Dao
interface AppelConsultationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(consultations: List<AppelConsultationEntity>) {
        Log.d("AppelConsultationDao", "Inserting ${consultations.size} items into database")
        if (consultations.isNotEmpty()) {
            Log.d("AppelConsultationDao", "First item being inserted - ID: ${consultations.first().cle_appel_consultation}, " +
                    "Name: ${consultations.first().nom_appel_consultation}")
        }
        _insertAll(consultations)
    }
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun _insertAll(consultations: List<AppelConsultationEntity>)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(consultation: AppelConsultationEntity)
    
    @Update
    suspend fun update(consultation: AppelConsultationEntity)

    @Query("""
        SELECT * FROM appel_consultation 
        WHERE (:query = '' OR 
               nom_appel_consultation LIKE '%' || :query || '%' OR
               cle_appel_consultation LIKE '%' || :query || '%')
          AND (:dateQuery = '' OR date_depot LIKE '%' || :dateQuery || '%')
        ORDER BY CAST(cle_appel_consultation AS INTEGER) DESC
    """)
    fun getAppelConsultationPagingSource(
        query: String = "",
        dateQuery: String = ""
    ): PagingSource<Int, AppelConsultationEntity> {
        Log.d("AppelConsultationDao", "Creating PagingSource with query: '$query', dateQuery: '$dateQuery'")
        return object : PagingSource<Int, AppelConsultationEntity>() {
            override suspend fun load(params: LoadParams<Int>): LoadResult<Int, AppelConsultationEntity> {
                val page = params.key ?: 0
                val pageSize = params.loadSize
                
                Log.d("AppelConsultationDao", "Loading page $page with size $pageSize")
                
                return try {
                    val items = getAppelConsultations(
                        query = if (query == "%%") "%%" else query,
                        limit = pageSize,
                        offset = page * pageSize
                    )
                    
                    Log.d("AppelConsultationDao", "Loaded ${items.size} items from database")
                    if (items.isNotEmpty()) {
                        Log.d("AppelConsultationDao", "First item in page $page: ${items.first().cle_appel_consultation} - ${items.first().nom_appel_consultation}")
                    }
                    
                    LoadResult.Page(
                        data = items,
                        prevKey = if (page == 0) null else page - 1,
                        nextKey = if (items.size < pageSize) null else page + 1
                    )
                } catch (e: Exception) {
                    Log.e("AppelConsultationDao", "Error loading items: ${e.message}", e)
                    LoadResult.Error(e)
                }
            }
            
            override fun getRefreshKey(state: PagingState<Int, AppelConsultationEntity>): Int? {
                return state.anchorPosition?.let { anchorPosition ->
                    state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                        ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
                }
            }
        }
    }

    @Query("SELECT * FROM appel_consultation WHERE cle_appel_consultation = :id")
    suspend fun getAppelConsultationById(id: Int): AppelConsultationEntity?
    
    @Query("""
        SELECT * FROM appel_consultation 
        WHERE nom_appel_consultation LIKE :query 
        ORDER BY CAST(cle_appel_consultation AS INTEGER) DESC
        LIMIT :limit OFFSET :offset
    """)
    suspend fun getAppelConsultations(
        query: String,
        limit: Int,
        offset: Int
    ): List<AppelConsultationEntity>

    @Query("DELETE FROM appel_consultation")
    suspend fun clearAll()
    
    @Query("DELETE FROM appel_consultation")
    suspend fun _clearAll()
    
    @Query("DELETE FROM appel_consultation WHERE cle_appel_consultation = :id")
    suspend fun deleteById(id: Int)

    @Query("""
        SELECT COUNT(*) FROM appel_consultation 
        WHERE (:query = '' OR 
               nom_appel_consultation LIKE '%' || :query || '%' OR
               date_depot LIKE '%' || :query || '%' OR
               cle_appel_consultation LIKE '%' || :query || '%')
    """)
    suspend fun getCount(query: String = ""): Int
    
    @Query("SELECT COUNT(*) FROM appel_consultation")
    suspend fun getTotalCount(): Int
    
    @Query("SELECT EXISTS(SELECT * FROM appel_consultation WHERE cle_appel_consultation = :id)")
    suspend fun isIdExists(id: Int): Boolean
    
    @Query("SELECT * FROM appel_consultation WHERE cle_appel_consultation = :key")
    suspend fun getByKey(key: String): AppelConsultationEntity?
    
    @Query("SELECT * FROM appel_consultation ORDER BY CAST(cle_appel_consultation AS INTEGER) DESC LIMIT 1")
    suspend fun getFirstConsultation(): AppelConsultationEntity?
}
