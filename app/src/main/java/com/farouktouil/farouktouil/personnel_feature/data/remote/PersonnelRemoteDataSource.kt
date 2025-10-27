package com.farouktouil.farouktouil.personnel_feature.data.remote

import com.farouktouil.farouktouil.personnel_feature.domain.model.Personnel

class PersonnelRemoteDataSource(private val personnelApiService: PersonnelApiService) {
    suspend fun getPersonnel(): List<Personnel> = personnelApiService.getPersonnel()
}
