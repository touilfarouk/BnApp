package com.farouktouil.farouktouil.consultation_feature.data.local.dao

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
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
    @Query("SELECT * FROM appel_consultation WHERE cle_appel_consultation = :id")
    suspend fun getConsultationById(id: Int): AppelConsultationEntity?
    
    @Query("SELECT * FROM appel_consultation WHERE cle_appel_consultation = :key")
    suspend fun getByKey(key: String): AppelConsultationEntity?
    
    @Query("SELECT * FROM appel_consultation ORDER BY cle_appel_consultation DESC LIMIT 1")
    suspend fun getFirstConsultation(): AppelConsultationEntity?
    
    // Get with relationships
    @Transaction
    @Query("SELECT * FROM appel_consultation WHERE cle_appel_consultation = :id")
    suspend fun getConsultationWithDocuments(id: Int): AppelConsultationWithDocuments?
    
    // Get all operations
    @Query("SELECT * FROM appel_consultation ORDER BY cle_appel_consultation DESC")
    fun getAllConsultations(): Flow<List<AppelConsultationEntity>>
    
    // Search operations
    @Query("""
        SELECT * FROM appel_consultation 
        WHERE (:query = '' OR 
               nom_appel_consultation LIKE '%' || :query || '%' OR
               cle_appel_consultation LIKE '%' || :query || '%')
          AND (:dateQuery = '' OR date_depot LIKE '%' || :dateQuery || '%')
        ORDER BY CAST(cle_appel_consultation AS INTEGER) DESC
        LIMIT :limit OFFSET :offset
    """)
    suspend fun searchConsultations(
        query: String = "",
        dateQuery: String = "",
        limit: Int,
        offset: Int
    ): List<AppelConsultationEntity>
    
    // Delete operations
    @Query("DELETE FROM appel_consultation WHERE cle_appel_consultation = :id")
    suspend fun deleteById(id: Int)
    
    @Query("""
        DELETE FROM appel_consultation 
        WHERE nom_appel_consultation LIKE '%' || :query || '%' 
           OR cle_appel_consultation LIKE '%' || :query || '%'
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
               nom_appel_consultation LIKE '%' || :query || '%' OR
               date_depot LIKE '%' || :query || '%' OR
               cle_appel_consultation LIKE '%' || :query || '%')
    """)
    suspend fun getCount(query: String = ""): Int
    
    // Check if exists
    @Query("SELECT EXISTS(SELECT * FROM appel_consultation WHERE cle_appel_consultation = :id)")
    suspend fun isIdExists(id: Int): Boolean
    
    // Paging source
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
    ): PagingSource<Int, AppelConsultationEntity>
}
