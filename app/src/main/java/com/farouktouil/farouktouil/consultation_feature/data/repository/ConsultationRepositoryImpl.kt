package com.farouktouil.farouktouil.consultation_feature.data.repository

import android.util.Log
import com.farouktouil.farouktouil.consultation_feature.domain.model.AppelConsultation
import com.farouktouil.farouktouil.consultation_feature.domain.repository.ConsultationRepository
import com.farouktouil.farouktouil.consultation_feature.data.remote.ConsultationRemoteDataSource

class ConsultationRepositoryImpl(
    private val consultationRemoteDataSource: ConsultationRemoteDataSource
) : ConsultationRepository {

    override suspend fun getConsultationCalls(): Result<List<AppelConsultation>> {
        return try {
            val consultations = consultationRemoteDataSource.getConsultationCalls()
            Log.d("ConsultationRepository", "Consultation calls fetched successfully: ${consultations.size} items")
            Result.success(consultations)
        } catch (e: Exception) {
            Log.e("ConsultationRepository", "Error fetching consultation calls", e)
            Result.failure(e)
        }
    }
}
