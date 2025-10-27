package com.farouktouil.farouktouil.personnel_feature.domain.use_case

import com.farouktouil.farouktouil.personnel_feature.domain.model.Personnel
import com.farouktouil.farouktouil.personnel_feature.domain.repository.PersonnelRepository

class GetPersonnelUseCase(
    private val personnelRepository: PersonnelRepository
) {
    suspend operator fun invoke(): Result<List<Personnel>> {
        return personnelRepository.getPersonnel()
    }
}
