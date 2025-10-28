package com.farouktouil.farouktouil.consultation_feature.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.farouktouil.farouktouil.consultation_feature.data.local.entity.AppelConsultationEntity

@Dao
interface AppelConsultationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(repos: List<AppelConsultationEntity>)

    @Query("SELECT * FROM appel_consultation WHERE nom_appel_consultation LIKE :queryString OR date_depot LIKE :queryString")
    fun getAppelConsultationPagingSource(queryString: String): PagingSource<Int, AppelConsultationEntity>

    @Query("DELETE FROM appel_consultation")
    suspend fun clearAll()

    @Query("SELECT COUNT(*) FROM appel_consultation WHERE nom_appel_consultation LIKE :queryString OR date_depot LIKE :queryString")
    suspend fun getCountCorrespondingToQuery(queryString: String): Int
}
