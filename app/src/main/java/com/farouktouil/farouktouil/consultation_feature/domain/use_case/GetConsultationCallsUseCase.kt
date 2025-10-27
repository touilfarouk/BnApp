package com.farouktouil.farouktouil.consultation_feature.domain.use_case

import com.farouktouil.farouktouil.consultation_feature.domain.model.AppelConsultation
import com.farouktouil.farouktouil.consultation_feature.domain.repository.ConsultationRepository

class GetConsultationCallsUseCase(
    private val consultationRepository: ConsultationRepository
) {
    suspend operator fun invoke(): Result<List<AppelConsultation>> {
        return consultationRepository.getConsultationCalls()
    }
}
