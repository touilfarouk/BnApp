package com.farouktouil.farouktouil.consultation_feature.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.farouktouil.farouktouil.consultation_feature.data.local.entity.DocumentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DocumentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(document: DocumentEntity): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(documents: List<DocumentEntity>)
    
    @Query("SELECT * FROM document_entity WHERE consultationId = :consultationId")
    fun getDocumentsByConsultationId(consultationId: Int): Flow<List<DocumentEntity>>
    
    @Query("SELECT * FROM document_entity WHERE id = :documentId")
    suspend fun getDocumentById(documentId: Int): DocumentEntity?
    
    @Delete
    suspend fun delete(document: DocumentEntity)
    
    @Query("DELETE FROM document_entity WHERE consultationId = :consultationId")
    suspend fun deleteByConsultationId(consultationId: Int)
    
    @Query("DELETE FROM document_entity")
    suspend fun deleteAll()
}
