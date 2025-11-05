package com.farouktouil.farouktouil.consultation_feature.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.farouktouil.farouktouil.consultation_feature.data.local.entity.RemoteKey

@Dao
interface RemoteKeysDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKeys: List<RemoteKey>)

    @Query("SELECT * FROM consultation_remote_keys WHERE consultationId = :consultationId LIMIT 1")
    suspend fun remoteKeysByConsultationId(consultationId: Int): RemoteKey?

    @Query("DELETE FROM consultation_remote_keys")
    suspend fun clearRemoteKeys()
    
    @Query("DELETE FROM consultation_remote_keys WHERE consultationId = :consultationId")
    suspend fun deleteByConsultationId(consultationId: Int)
    
    @Query("SELECT * FROM consultation_remote_keys WHERE consultationId = :consultationId AND (query = :query OR :query IS NULL) LIMIT 1")
    suspend fun getRemoteKeys(consultationId: Int, query: String? = null): RemoteKey?
    
    @Query("SELECT * FROM consultation_remote_keys WHERE (query = :query OR :query IS NULL) ORDER BY id DESC LIMIT 1")
    suspend fun getLastRemoteKey(query: String? = null): RemoteKey?
}