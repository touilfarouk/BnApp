package com.farouktouil.farouktouil.personnel_feature.data.remote

import com.farouktouil.farouktouil.personnel_feature.domain.model.Personnel
import retrofit2.http.GET
import retrofit2.http.Query

interface PersonnelApiService {
    @GET("personnel.php")
    suspend fun getPersonnel(
        @Query("page") page: Int,
        @Query("name") name: String? = null,
        @Query("structure") structure: String? = null,
        @Query("active") active: Int? = null
    ): List<Personnel>
}
