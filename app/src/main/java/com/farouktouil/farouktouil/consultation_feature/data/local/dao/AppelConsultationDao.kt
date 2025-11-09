package com.farouktouil.farouktouil.consultation_feature.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.farouktouil.farouktouil.consultation_feature.data.local.entity.AppelConsultationEntity
import com.farouktouil.farouktouil.consultation_feature.data.local.entity.AppelConsultationWithDocuments
import kotlinx.coroutines.flow.Flow

@Dao
interface AppelConsultationDao {
    // Insert operations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(consultations: List<AppelConsultationEntity>)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(consultation: AppelConsultationEntity)
    
    // Update operation
    @Update
    suspend fun update(consultation: AppelConsultationEntity)
    
    // Get operations
    @Query("SELECT * FROM appel_consultation WHERE id = :id")
    suspend fun getConsultationById(id: Int): AppelConsultationEntity?
    
    @Query("SELECT * FROM appel_consultation WHERE id = :key")
    suspend fun getByKey(key: String): AppelConsultationEntity?
    
    @Query("SELECT * FROM appel_consultation ORDER BY id DESC LIMIT 1")
    suspend fun getFirstConsultation(): AppelConsultationEntity?
    
    // Get with relationships
    @Transaction
    @Query("SELECT * FROM appel_consultation WHERE id = :id")
    suspend fun getConsultationWithDocuments(id: Int): AppelConsultationWithDocuments?
    
    // Get all operations
    @Query("SELECT * FROM appel_consultation ORDER BY id DESC")
    fun getAllConsultations(): Flow<List<AppelConsultationEntity>>
    
    // Search operations
    @Query("""
        SELECT * FROM appel_consultation 
        WHERE (:query = '' OR 
               title LIKE '%' || :query || '%' OR
               CAST(id AS TEXT) LIKE '%' || :query || '%')
          AND (:dateQuery = '' OR depositDate LIKE '%' || :dateQuery || '%')
        ORDER BY id DESC
        LIMIT :limit OFFSET :offset
    """)
    suspend fun searchConsultations(
        query: String = "",
        dateQuery: String = "",
        limit: Int,
        offset: Int
    ): List<AppelConsultationEntity>
    
    // Delete operations
    @Query("DELETE FROM appel_consultation WHERE id = :id")
    suspend fun deleteById(id: Int)
    
    @Query("""
        DELETE FROM appel_consultation 
        WHERE title LIKE '%' || :query || '%' 
           OR CAST(id AS TEXT) LIKE '%' || :query || '%'
    """)
    suspend fun deleteByQuery(query: String)
    
    @Query("DELETE FROM appel_consultation")
    suspend fun clearAll()
    
    // Count operations
    @Query("SELECT COUNT(*) FROM appel_consultation")
    suspend fun getTotalCount(): Int
    
    @Query("""
        SELECT COUNT(*) FROM appel_consultation 
        WHERE (:query = '' OR 
               title LIKE '%' || :query || '%' OR
               depositDate LIKE '%' || :query || '%' OR
               CAST(id AS TEXT) LIKE '%' || :query || '%')
    """)
    suspend fun getCount(query: String = ""): Int
    
    // Check if exists
    @Query("SELECT EXISTS(SELECT * FROM appel_consultation WHERE id = :id)")
    suspend fun isIdExists(id: Int): Boolean
    
    @Transaction
    @Query("""
        SELECT * FROM appel_consultation 
        WHERE title LIKE :query
        ORDER BY id DESC
        LIMIT :limit OFFSET :offset
    """)
    suspend fun getConsultationsWithDocuments(
        query: String,
        limit: Int,
        offset: Int
    ): List<AppelConsultationWithDocuments>
    
    // Paging source
    @Query("""
        SELECT * FROM appel_consultation 
        WHERE (:query = '' OR 
               title LIKE '%' || :query || '%' OR
               CAST(id AS TEXT) LIKE '%' || :query || '%')
          AND (:dateQuery = '' OR depositDate LIKE '%' || :dateQuery || '%')
        ORDER BY id DESC
    """)
    fun getAppelConsultationPagingSource(
        query: String = "",
        dateQuery: String = ""
    ): PagingSource<Int, AppelConsultationEntity>
}
