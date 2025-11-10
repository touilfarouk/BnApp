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

    @Query(
        """
        SELECT *
        FROM personnel
        WHERE (:nameQuery IS NULL OR username LIKE :nameQuery OR nom LIKE :nameQuery OR prenom LIKE :nameQuery)
          AND (:structureQuery IS NULL OR structure LIKE :structureQuery)
          AND (:activeStatus IS NULL OR active = :activeStatus)
        ORDER BY 
            CASE WHEN structure IS NULL OR structure = '' THEN 1 ELSE 0 END,
            structure COLLATE NOCASE,
            nom COLLATE NOCASE,
            prenom COLLATE NOCASE
        """
    )
    fun getPersonnel(
        nameQuery: String?,
        structureQuery: String?,
        activeStatus: Int?
    ): PagingSource<Int, PersonnelEntity>

    @Query("SELECT COUNT(*) FROM personnel")
    suspend fun countAll(): Int

    @Query("DELETE FROM personnel")
    suspend fun clearAll()
}
