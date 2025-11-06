package com.farouktouil.farouktouil.consultation_feature.data.repository

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.farouktouil.farouktouil.consultation_feature.data.mapper.toEntity
import androidx.paging.PagingData
import androidx.paging.map
import com.farouktouil.farouktouil.consultation_feature.data.local.dao.AppelConsultationDao
import com.farouktouil.farouktouil.consultation_feature.data.mapper.toDomain
import com.farouktouil.farouktouil.consultation_feature.data.mapper.toEntities
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
    private val consultationApiService: ConsultationApiService,
    private val networkUtils: com.farouktouil.farouktouil.core.util.NetworkUtils
) : ConsultationRepository {

    private val appelConsultationDao: AppelConsultationDao
        get() = appDatabase.appelConsultationDao()

    @OptIn(ExperimentalPagingApi::class)
    override fun getConsultationCalls(searchQuery: ConsultationSearchQuery): Flow<PagingData<AppelConsultation>> {
        val nomAppelConsultation = searchQuery.nom_appel_consultation ?: ""
        val query = "%$nomAppelConsultation%"
        Log.d("ConsultationRepo", "Getting consultation calls with query: $query")
        
        val pagingSourceFactory = { 
            Log.d("ConsultationRepo", "Creating new PagingSource")
            appelConsultationDao.getAppelConsultationPagingSource(query) 
        }
        
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
                searchQuery = searchQuery,
                networkUtils = networkUtils
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow.map { pagingData ->
            pagingData.map { it.toDomain() }
        }
    }

    override suspend fun saveConsultation(consultation: AppelConsultation): Long {
        return try {
            val entity = consultation.toEntity()
            if (consultation.id != 0 && appelConsultationDao.isIdExists(consultation.id)) {
                appelConsultationDao.update(entity)
                consultation.id.toLong()
            } else {
                appelConsultationDao.insert(entity)
            }
            
            // In a real app, you would also save to the server here
            // and update the local database with the server response
            
            consultation.id.toLong()
        } catch (e: Exception) {
            // Log error
            -1L
        }
    }

    override suspend fun deleteConsultation(consultation: AppelConsultation) {
        try {
            appelConsultationDao.deleteById(consultation.id)
            
            // In a real app, you would also delete from the server here
        } catch (e: Exception) {
            throw Exception("Failed to delete consultation: ${e.message}")
        }
    }

    override suspend fun getConsultationById(id: Int): AppelConsultation? {
        return try {
            // First try to get from local database
            val local = appDatabase.appelConsultationDao().getConsultationById(id)
            
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
        return appelConsultationDao.getByKey(key)?.toDomain()
    }
    
    override suspend fun logFirstConsultation() {
        try {
            val firstConsultation = appelConsultationDao.getFirstConsultation()
            if (firstConsultation != null) {
                Log.d("ConsultationRepo", "First offline consultation: $firstConsultation")
                Log.d("ConsultationRepo", "Details - ID: ${firstConsultation.cle_appel_consultation}, " +
                        "Name: ${firstConsultation.nom_appel_consultation}, " +
                        "Date: ${firstConsultation.date_depot}")
            } else {
                Log.d("ConsultationRepo", "No consultations found in local database")
            }
        } catch (e: Exception) {
            Log.e("ConsultationRepo", "Error logging first consultation", e)
        }
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
