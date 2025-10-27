package com.farouktouil.farouktouil.personnel_feature.data.repository

import android.util.Log
import com.farouktouil.farouktouil.personnel_feature.domain.model.Personnel
import com.farouktouil.farouktouil.personnel_feature.domain.repository.PersonnelRepository
import com.farouktouil.farouktouil.personnel_feature.data.remote.PersonnelRemoteDataSource

class PersonnelRepositoryImpl(
    private val personnelRemoteDataSource: PersonnelRemoteDataSource
) : PersonnelRepository {

    override suspend fun getPersonnel(): Result<List<Personnel>> {
        return try {
            val personnel = personnelRemoteDataSource.getPersonnel()
            Log.d("PersonnelRepository", "Personnel fetched successfully: ${personnel.size} items")
            Result.success(personnel)
        } catch (e: Exception) {
            Log.e("PersonnelRepository", "Error fetching personnel", e)
            Result.failure(e)
        }
    }
}
