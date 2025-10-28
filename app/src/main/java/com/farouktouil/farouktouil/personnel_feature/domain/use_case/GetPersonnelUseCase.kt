package com.farouktouil.farouktouil.personnel_feature.domain.use_case

import androidx.paging.PagingData
import com.farouktouil.farouktouil.personnel_feature.domain.model.Personnel
import com.farouktouil.farouktouil.personnel_feature.domain.model.PersonnelSearchQuery
import com.farouktouil.farouktouil.personnel_feature.domain.repository.PersonnelRepository
import kotlinx.coroutines.flow.Flow

class GetPersonnelUseCase(
    private val personnelRepository: PersonnelRepository
) {
    operator fun invoke(searchQuery: PersonnelSearchQuery): Flow<PagingData<Personnel>> {
        return personnelRepository.getPersonnel(searchQuery)
    }
}
