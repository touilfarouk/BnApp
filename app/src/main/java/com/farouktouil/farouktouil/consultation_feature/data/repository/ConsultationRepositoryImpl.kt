package com.farouktouil.farouktouil.consultation_feature.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.farouktouil.farouktouil.consultation_feature.data.mapper.toDomain
import com.farouktouil.farouktouil.consultation_feature.data.mapper.toEntity
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

    override suspend fun saveConsultation(consultation: AppelConsultation): Long {
        return try {
            // First save to local database
            val entity = consultation.toEntity()
            val id = if (consultation.id != 0 && appDatabase.appelConsultationDao().isIdExists(consultation.id)) {
                // Update existing consultation
                appDatabase.appelConsultationDao().update(entity)
                consultation.id.toLong()
            } else {
                // Insert new consultation
                appDatabase.appelConsultationDao().insert(entity)
                entity.id.toLong()
            }
            
            // Then try to sync with remote
            try {
                // Call your API to save the consultation
                // Uncomment and implement when your API is ready
                // val response = consultationApiService.saveConsultation(consultation)
                // Handle response if needed
            } catch (e: Exception) {
                // If sync fails, the local data is still saved
                // You might want to implement a sync mechanism for later
            }
            
            id
        } catch (e: Exception) {
            throw Exception("Failed to save consultation: ${e.message}")
        }
    }

    override suspend fun deleteConsultation(consultation: AppelConsultation) {
        try {
            // First delete from local database
            appDatabase.appelConsultationDao().deleteById(consultation.id)
            
            // Then try to sync with remote
            try {
                // Call your API to delete the consultation
                // Uncomment and implement when your API is ready
                // consultationApiService.deleteConsultation(consultation.id)
            } catch (e: Exception) {
                // If sync fails, you might want to add the operation to a queue for later retry
                // or mark the record as deleted locally but keep it for sync
            }
        } catch (e: Exception) {
            throw Exception("Failed to delete consultation: ${e.message}")
        }
    }

    override suspend fun getConsultationById(id: Int): AppelConsultation? {
        return try {
            // First try to get from local database
            val local = appDatabase.appelConsultationDao().getAppelConsultationById(id)
            
            // If not found locally or data might be stale, try to fetch from remote
            if (local == null || isDataStale(local.lastUpdated)) {
                try {
                    // Uncomment and implement when your API is ready
                    // val remote = consultationApiService.getConsultationById(id)
                    // if (remote != null) {
                    //     val entity = remote.toEntity()
                    //     appDatabase.appelConsultationDao().insert(entity)
                    //     return entity.toDomain()
                    // }
                } catch (e: Exception) {
                    // If remote fetch fails, return local data if available
                    if (local != null) return local.toDomain()
                    throw Exception("Failed to fetch consultation: ${e.message}")
                }
            }
            
            local?.toDomain()
        } catch (e: Exception) {
            throw Exception("Failed to get consultation: ${e.message}")
        }
    }

    override suspend fun getConsultationByKey(key: String): AppelConsultation? {
        return try {
            // First try to get from local database
            val local = appDatabase.appelConsultationDao().getByKey(key)
            
            // If not found locally or data might be stale, try to fetch from remote
            if (local == null || isDataStale(local.lastUpdated)) {
                try {
                    // Uncomment and implement when your API is ready
                    // val remote = consultationApiService.getConsultationByKey(key)
                    // if (remote != null) {
                    //     val entity = remote.toEntity()
                    //     appDatabase.appelConsultationDao().insert(entity)
                    //     return entity.toDomain()
                    // }
                } catch (e: Exception) {
                    // If remote fetch fails, return local data if available
                    if (local != null) return local.toDomain()
                    throw Exception("Failed to fetch consultation by key: ${e.message}")
                }
            }
            
            local?.toDomain()
        } catch (e: Exception) {
            throw Exception("Failed to get consultation by key: ${e.message}")
        }
    }
    
    /**
     * Check if the data is stale based on last updated timestamp
     * @param lastUpdated Timestamp when the data was last updated
     * @return true if data is older than 1 hour
     */
    private fun isDataStale(lastUpdated: Long?): Boolean {
        if (lastUpdated == null) return true
        val oneHourInMillis = 60 * 60 * 1000L
        return (System.currentTimeMillis() - lastUpdated) > oneHourInMillis
    }
}
