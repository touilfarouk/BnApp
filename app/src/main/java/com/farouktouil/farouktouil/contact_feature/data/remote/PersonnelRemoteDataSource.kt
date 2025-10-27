package com.farouktouil.farouktouil.contact_feature.data.remote

import com.farouktouil.farouktouil.contact_feature.data.mapper.PersonnelMapper
import com.farouktouil.farouktouil.contact_feature.domain.model.Personnel

class PersonnelRemoteDataSource(
    private val apiService: PersonnelApiService
) {
    suspend fun getPersonnel(): List<Personnel> {
        return try {
            val response = apiService.getPersonnel()
            PersonnelMapper.toDomainList(response)
        } catch (e: Exception) {
            throw e
        }
    }
}
