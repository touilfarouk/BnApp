package com.farouktouil.farouktouil.personnel_feature.domain.use_case

import com.farouktouil.farouktouil.personnel_feature.domain.model.Personnel
import com.farouktouil.farouktouil.personnel_feature.domain.repository.PersonnelRepository
import javax.inject.Inject

class GetPersonnelDirectoryUseCase @Inject constructor(
    private val personnelRepository: PersonnelRepository
) {
    suspend operator fun invoke(): List<Personnel> {
        return personnelRepository.getPersonnelDirectory()
    }
}
