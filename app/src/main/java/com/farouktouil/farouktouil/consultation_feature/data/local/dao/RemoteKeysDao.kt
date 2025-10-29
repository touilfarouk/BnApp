package com.farouktouil.farouktouil.consultation_feature.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.farouktouil.farouktouil.consultation_feature.data.local.entity.RemoteKey

@Dao
interface RemoteKeysDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKey: List<RemoteKey>)

    @Query("SELECT * FROM consultation_remote_keys WHERE id = :id")
    suspend fun remoteKeysByConsultationId(id: String): RemoteKey?

    @Query("DELETE FROM consultation_remote_keys")
    suspend fun clearRemoteKeys()
    
    @Query("DELETE FROM consultation_remote_keys WHERE id = :id")
    suspend fun deleteRemoteKeyById(id: String)
    
    @Query("SELECT * FROM consultation_remote_keys WHERE id = :id AND (query = :query OR :query IS NULL)")
    suspend fun getRemoteKeys(id: String, query: String? = null): RemoteKey?
}
