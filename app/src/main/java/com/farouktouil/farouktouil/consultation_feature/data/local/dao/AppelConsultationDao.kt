package com.farouktouil.farouktouil.consultation_feature.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.farouktouil.farouktouil.consultation_feature.data.local.entity.AppelConsultationEntity

@Dao
interface AppelConsultationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(consultations: List<AppelConsultationEntity>)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(consultation: AppelConsultationEntity)
    
    @Update
    suspend fun update(consultation: AppelConsultationEntity)

    @Query("""
        SELECT * FROM appel_consultation 
        WHERE (:query = '' OR 
               nom_appel_consultation LIKE '%' || :query || '%' OR
               date_depot LIKE '%' || :query || '%' OR
               cle_appel_consultation LIKE '%' || :query || '%')
        ORDER BY 
            CASE 
                WHEN :orderBy = 'date_asc' AND date_depot IS NOT NULL THEN date_depot 
                WHEN :orderBy = 'date_desc' AND date_depot IS NOT NULL THEN date_depot 
                ELSE nom_appel_consultation 
            END
            COLLATE NOCASE DESC
    """)
    fun getAppelConsultationPagingSource(
        query: String = "",
        orderBy: String = "date_desc"
    ): PagingSource<Int, AppelConsultationEntity>

    @Query("SELECT * FROM appel_consultation WHERE id = :id")
    suspend fun getAppelConsultationById(id: Int): AppelConsultationEntity?

    @Query("DELETE FROM appel_consultation")
    suspend fun clearAll()
    
    @Query("DELETE FROM appel_consultation WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("""
        SELECT COUNT(*) FROM appel_consultation 
        WHERE (:query = '' OR 
               nom_appel_consultation LIKE '%' || :query || '%' OR
               date_depot LIKE '%' || :query || '%' OR
               cle_appel_consultation LIKE '%' || :query || '%')
    """)
    suspend fun getCount(query: String = ""): Int
    
    @Query("SELECT COUNT(*) FROM appel_consultation")
    suspend fun getTotalCount(): Int
    
    @Query("SELECT EXISTS(SELECT * FROM appel_consultation WHERE id = :id)")
    suspend fun isIdExists(id: Int): Boolean
    
    @Query("SELECT * FROM appel_consultation WHERE cle_appel_consultation = :key")
    suspend fun getByKey(key: String): AppelConsultationEntity?
}
