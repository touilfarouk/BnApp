package com.farouktouil.farouktouil.consultation_feature.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.farouktouil.farouktouil.consultation_feature.data.local.entity.AppelConsultationEntity
import com.farouktouil.farouktouil.consultation_feature.data.mapper.toDomain
import com.farouktouil.farouktouil.consultation_feature.data.remote.AppelConsultationRemoteMediator
import com.farouktouil.farouktouil.consultation_feature.data.remote.ConsultationApiService
import com.farouktouil.farouktouil.consultation_feature.domain.model.AppelConsultation
import com.farouktouil.farouktouil.consultation_feature.domain.model.ConsultationSearchQuery
import com.farouktouil.farouktouil.consultation_feature.domain.repository.ConsultationRepository
import com.farouktouil.farouktouil.core.data.local.AppDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ConsultationRepositoryImpl @Inject constructor(
    private val appDatabase: AppDatabase,
    private val consultationApiService: ConsultationApiService
) : ConsultationRepository {

    @OptIn(ExperimentalPagingApi::class)
    override fun getConsultationCalls(searchQuery: ConsultationSearchQuery): Flow<PagingData<AppelConsultation>> {
        val nomQuery = if (searchQuery.nom_appel_consultation.isNullOrEmpty()) "" else "%${searchQuery.nom_appel_consultation}%"
        val dateQuery = if (searchQuery.date_depot.isNullOrEmpty()) "" else "%${searchQuery.date_depot}%"
        
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false,
                prefetchDistance = 5,
                initialLoadSize = 20
            ),
            remoteMediator = AppelConsultationRemoteMediator(
                appDatabase = appDatabase,
                consultationApiService = consultationApiService,
                searchQuery = searchQuery
            ),
            pagingSourceFactory = {
                appDatabase.appelConsultationDao().getAppelConsultationPagingSource(nomQuery, dateQuery)
            }
        ).flow.map { pagingData ->
            pagingData.map { it.toDomain() }
        }
    }
}
