package com.farouktouil.farouktouil.consultation_feature.domain.repository

import androidx.paging.PagingData
import com.farouktouil.farouktouil.consultation_feature.domain.model.AppelConsultation
import com.farouktouil.farouktouil.consultation_feature.domain.model.ConsultationSearchQuery
import kotlinx.coroutines.flow.Flow

interface ConsultationRepository {
    fun getConsultationCalls(searchQuery: ConsultationSearchQuery): Flow<PagingData<AppelConsultation>>
}
