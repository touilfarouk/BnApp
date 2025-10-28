package com.farouktouil.farouktouil.personnel_feature.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.farouktouil.farouktouil.personnel_feature.data.local.PersonnelEntity

@Dao
interface PersonnelDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(personnel: List<PersonnelEntity>)

    @Query("SELECT * FROM personnel WHERE nom LIKE :query OR structure LIKE :query")
    fun getPersonnel(query: String): PagingSource<Int, PersonnelEntity>

    @Query("SELECT COUNT(*) FROM personnel WHERE nom LIKE :query OR structure LIKE :query")
    suspend fun getCountCorrespondingToQuery(query: String): Int

    @Query("DELETE FROM personnel")
    suspend fun clearAll()
}
