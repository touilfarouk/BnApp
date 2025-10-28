package com.farouktouil.farouktouil.consultation_feature.data.remote

import com.farouktouil.farouktouil.consultation_feature.domain.model.AppelConsultation
import retrofit2.http.GET
import retrofit2.http.Query

interface ConsultationApiService {
    @GET("tender-calls.php")
    suspend fun getConsultationCalls(
        @Query("page") page: Int,
        @Query("nom_appel_consultation") nom_appel_consultation: String?,
        @Query("date_depot") date_depot: String?
    ): List<AppelConsultation>
}
