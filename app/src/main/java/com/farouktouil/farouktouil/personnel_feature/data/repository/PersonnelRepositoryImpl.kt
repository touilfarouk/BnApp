package com.farouktouil.farouktouil.personnel_feature.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.farouktouil.farouktouil.core.data.local.AppDatabase
import com.farouktouil.farouktouil.personnel_feature.data.mapper.toDomain
import com.farouktouil.farouktouil.personnel_feature.data.remote.PersonnelApiService
import com.farouktouil.farouktouil.personnel_feature.data.remote.PersonnelRemoteMediator
import com.farouktouil.farouktouil.personnel_feature.domain.model.Personnel
import com.farouktouil.farouktouil.personnel_feature.domain.model.PersonnelSearchQuery
import com.farouktouil.farouktouil.personnel_feature.domain.repository.PersonnelRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class PersonnelRepositoryImpl @Inject constructor(
    private val appDatabase: AppDatabase,
    private val personnelApiService: PersonnelApiService
) : PersonnelRepository {

    override fun getPersonnel(searchQuery: PersonnelSearchQuery): Flow<PagingData<Personnel>> {
        val nameQuery = searchQuery.name?.trim()?.takeIf { it.isNotEmpty() }?.let { "%$it%" }
        val structureQuery = searchQuery.structure?.trim()?.takeIf { it.isNotEmpty() }?.let { "%$it%" }
        val pagingSourceFactory = {
            appDatabase.personnelDao().getPersonnel(
                nameQuery = nameQuery,
                structureQuery = structureQuery,
                activeStatus = searchQuery.active
            )
        }

        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            remoteMediator = PersonnelRemoteMediator(
                searchQuery = searchQuery,
                appDatabase = appDatabase,
                personnelApiService = personnelApiService
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow.map {
            it.map { it.toDomain() }
        }
    }
}
