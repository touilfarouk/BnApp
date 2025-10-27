package com.farouktouil.farouktouil.consultation_feature.data.remote

import com.farouktouil.farouktouil.consultation_feature.domain.model.AppelConsultation
import retrofit2.http.GET

interface ConsultationApiService {
    @GET("tender-calls.php")
    suspend fun getConsultationCalls(): List<AppelConsultation>
}
