package com.farouktouil.farouktouil.personnel_feature.data.remote


import com.farouktouil.farouktouil.personnel_feature.domain.model.Personnel
import retrofit2.http.GET

interface PersonnelApiService {
    @GET("personnel.php")
    suspend fun getPersonnel(): List<Personnel>
}
