package com.farouktouil.farouktouil.consultation_feature.data.remote

import com.farouktouil.farouktouil.consultation_feature.domain.model.AppelConsultation

class ConsultationRemoteDataSource(
    private val apiService: ConsultationApiService
) {
    suspend fun getConsultationCalls(): List<AppelConsultation> {
        return try {
            apiService.getConsultationCalls()
        } catch (e: Exception) {
            throw e
        }
    }
}
