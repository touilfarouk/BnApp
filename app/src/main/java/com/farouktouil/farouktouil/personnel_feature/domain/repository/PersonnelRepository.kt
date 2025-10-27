package com.farouktouil.farouktouil.personnel_feature.domain.repository

import com.farouktouil.farouktouil.personnel_feature.domain.model.Personnel

interface PersonnelRepository {

    suspend fun getPersonnel(): Result<List<Personnel>>
}
