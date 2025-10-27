package com.farouktouil.farouktouil.contact_feature.data.remote

import com.farouktouil.farouktouil.contact_feature.domain.model.Personnel
import retrofit2.http.GET

interface PersonnelApiService {
    @GET("personel.php")
    suspend fun getPersonnel(): List<Personnel>
}
