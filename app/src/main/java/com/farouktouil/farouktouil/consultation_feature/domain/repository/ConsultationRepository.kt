package com.farouktouil.farouktouil.consultation_feature.domain.repository

import com.farouktouil.farouktouil.consultation_feature.domain.model.AppelConsultation

interface ConsultationRepository {
    suspend fun getConsultationCalls(): Result<List<AppelConsultation>>
}
