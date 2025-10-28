package com.farouktouil.farouktouil.personnel_feature.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.farouktouil.farouktouil.personnel_feature.data.local.entities.RemoteKey

@Dao
interface RemoteKeysDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKey: List<RemoteKey>)

    // For Personnel Feature
    @Query("SELECT * FROM remote_keys WHERE id = :id AND `query` = :query")
    suspend fun getRemoteKeys(id: String, query: String): RemoteKey?

    // For Personnel Feature
    @Query("DELETE FROM remote_keys WHERE `query` = :query")
    suspend fun clearRemoteKeys(query: String)

    // For Consultation Feature
    @Query("SELECT * FROM remote_keys WHERE id = :id")
    suspend fun getRemoteKeyById(id: String): RemoteKey?

    // For Consultation Feature
    @Query("DELETE FROM remote_keys WHERE id = :id")
    suspend fun deleteRemoteKeyById(id: String)
}
