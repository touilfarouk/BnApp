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

    @Query("SELECT * FROM document_entity WHERE consultationId = :consultationId")
    suspend fun getDocumentsSnapshotByConsultationId(consultationId: Int): List<DocumentEntity>

    @Query("SELECT * FROM document_entity WHERE consultationId IN (:consultationIds)")
    suspend fun getDocumentsByConsultationIds(consultationIds: List<Int>): List<DocumentEntity>

    @Query(
        "SELECT * FROM document_entity WHERE consultationId = :consultationId AND fileUrl = :fileUrl LIMIT 1"
    )
    suspend fun getDocumentByConsultationAndUrl(consultationId: Int, fileUrl: String): DocumentEntity?

    @Query(
        "UPDATE document_entity SET localFilePath = :localFilePath, fileSize = :fileSize, lastUpdated = :lastUpdated " +
            "WHERE consultationId = :consultationId AND fileUrl = :fileUrl"
    )
    suspend fun updateDocumentCache(
        consultationId: Int,
        fileUrl: String,
        localFilePath: String?,
        fileSize: Long?,
        lastUpdated: Long
    )
    
    @Query("SELECT * FROM document_entity WHERE id = :documentId")
    suspend fun getDocumentById(documentId: Int): DocumentEntity?
    
    @Delete
    suspend fun delete(document: DocumentEntity)
    
    @Query("DELETE FROM document_entity WHERE consultationId = :consultationId")
    suspend fun deleteByConsultationId(consultationId: Int)
    
    @Query("DELETE FROM document_entity WHERE consultationId = :consultationId AND fileUrl NOT IN (:fileUrls)")
    suspend fun deleteDocumentsNotIn(consultationId: Int, fileUrls: List<String>)

    @Query("DELETE FROM document_entity")
    suspend fun deleteAll()
}
