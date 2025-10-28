package com.farouktouil.farouktouil.consultation_feature.domain.use_case

import androidx.paging.PagingData
import com.farouktouil.farouktouil.consultation_feature.domain.model.AppelConsultation
import com.farouktouil.farouktouil.consultation_feature.domain.model.ConsultationSearchQuery
import com.farouktouil.farouktouil.consultation_feature.domain.repository.ConsultationRepository
import kotlinx.coroutines.flow.Flow

class GetConsultationCallsUseCase(
    private val consultationRepository: ConsultationRepository
) {
    operator fun invoke(searchQuery: ConsultationSearchQuery): Flow<PagingData<AppelConsultation>> {
        return consultationRepository.getConsultationCalls(searchQuery)
    }
}
