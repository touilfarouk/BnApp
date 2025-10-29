package com.farouktouil.farouktouil.consultation_feature.data.local.dao

import androidx.paging.PagingSource
import androidx
.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.farouktouil.farouktouil.consultation_feature.data.local.entity.AppelConsultationEntity

@Dao
interface AppelConsultationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(repos: List<AppelConsultationEntity>)

    @Query("SELECT * FROM appel_consultation WHERE (:nomQuery = '' OR nom_appel_consultation LIKE :nomQuery) AND (:dateQuery = '' OR date_depot LIKE :dateQuery)")
    fun getAppelConsultationPagingSource(nomQuery: String, dateQuery: String): PagingSource<Int, AppelConsultationEntity>

    @Query("DELETE FROM appel_consultation")
    suspend fun clearAll()

    @Query("SELECT COUNT(*) FROM appel_consultation WHERE (:nomQuery = '' OR nom_appel_consultation LIKE :nomQuery) AND (:dateQuery = '' OR date_depot LIKE :dateQuery)")
    suspend fun getCountCorrespondingToQuery(nomQuery: String, dateQuery: String): Int
    
    @Query("SELECT EXISTS(SELECT * FROM appel_consultation WHERE id = :id)")
    suspend fun isIdExists(id: Int): Boolean
}
