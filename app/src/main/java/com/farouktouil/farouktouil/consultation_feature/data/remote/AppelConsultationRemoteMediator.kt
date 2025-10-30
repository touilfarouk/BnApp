package com.farouktouil.farouktouil.consultation_feature.data.remote

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.farouktouil.farouktouil.consultation_feature.data.local.entity.AppelConsultationEntity
import com.farouktouil.farouktouil.consultation_feature.data.mapper.toEntity
import com.farouktouil.farouktouil.consultation_feature.data.remote.dto.AppelConsultationDto
import com.farouktouil.farouktouil.consultation_feature.domain.model.AppelConsultation
import com.farouktouil.farouktouil.consultation_feature.domain.model.ConsultationSearchQuery
import com.farouktouil.farouktouil.core.data.local.AppDatabase
import com.farouktouil.farouktouil.consultation_feature.data.local.dao.RemoteKeysDao
import com.farouktouil.farouktouil.consultation_feature.data.local.entity.RemoteKey
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class AppelConsultationRemoteMediator(
    private val appDatabase: AppDatabase,
    private val consultationApiService: ConsultationApiService,
    private val searchQuery: ConsultationSearchQuery
) : RemoteMediator<Int, AppelConsultationEntity>() {

    private val appelConsultationDao = appDatabase.appelConsultationDao()
    private val remoteKeyDao: RemoteKeysDao = appDatabase.consultationRemoteKeysDao()
    private val searchId = searchQuery.toString()
    
    private fun getQueryString(): String {
        return "${searchQuery.nom_appel_consultation}_${searchQuery.date_depot}"
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, AppelConsultationEntity>
    ): MediatorResult {
        return try {
            val currentPage = when (loadType) {
                LoadType.REFRESH -> 1
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val remoteKey = appDatabase.withTransaction {
                        remoteKeyDao.getRemoteKeys(id = "consultation", query = getQueryString())
                    }
                    remoteKey?.nextKey ?: return MediatorResult.Success(endOfPaginationReached = true)
                }
            }

            // Log the request parameters
            android.util.Log.d("RemoteMediator", "Fetching page $currentPage with query: $searchQuery")
            
            val response = consultationApiService.getConsultationCalls(
                page = currentPage,
                nom_appel_consultation = searchQuery.nom_appel_consultation.takeIf { !it.isNullOrBlank() },
                date_depot = searchQuery.date_depot.takeIf { !it.isNullOrBlank() }
            )

            if (!response.isSuccessful) {
                return MediatorResult.Error(Exception("API call failed: ${response.code()} - ${response.message()}"))
            }

            val consultations = response.body() ?: emptyList()
            
            // Log the received data
            if (consultations.isNotEmpty()) {
                val firstItem = consultations.firstOrNull()
                val lastItem = consultations.lastOrNull()
                android.util.Log.d("RemoteMediator", "Received ${consultations.size} items")
                firstItem?.let { 
                    android.util.Log.d("RemoteMediator", "First item - Cle: ${it.cleAppelConsultation}, Nom: ${it.nomAppelConsultation}")
                }
                lastItem?.let {
                    if (it != firstItem) {
                        android.util.Log.d("RemoteMediator", "Last item - Cle: ${it.cleAppelConsultation}, Nom: ${it.nomAppelConsultation}")
                    }
                }
            } else {
                android.util.Log.d("RemoteMediator", "Received empty list of consultations")
            }
            
            val endOfPaginationReached = consultations.isEmpty()

            val appelConsultations = consultations.mapIndexed { index, dto ->
                dto.toAppelConsultation(currentPage * 20 + index) // Generate unique ID based on page and index
            }

            appDatabase.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    appelConsultationDao.clearAll()
                    remoteKeyDao.clearRemoteKeys()
                }

                // Insert new data into the database
                val entities = appelConsultations.map { it.toEntity() }
                appelConsultationDao.insertAll(entities)

                // Update remote keys
                val prevPage = if (currentPage == 1) null else currentPage - 1
                val nextPage = if (endOfPaginationReached) null else currentPage + 1
                
                remoteKeyDao.insertAll(
                    listOf(RemoteKey(
                        id = "consultation",
                        prevKey = prevPage,
                        nextKey = nextPage,
                        query = getQueryString()
                    ))
                )
            }

            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: IOException) {
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            MediatorResult.Error(e)
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }
}
