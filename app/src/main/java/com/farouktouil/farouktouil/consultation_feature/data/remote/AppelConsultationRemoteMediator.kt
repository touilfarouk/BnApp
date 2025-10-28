package com.farouktouil.farouktouil.consultation_feature.data.remote

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.farouktouil.farouktouil.consultation_feature.data.local.entity.AppelConsultationEntity
import com.farouktouil.farouktouil.consultation_feature.data.mapper.toEntities
import com.farouktouil.farouktouil.consultation_feature.domain.model.ConsultationSearchQuery
import com.farouktouil.farouktouil.core.data.local.AppDatabase
import com.farouktouil.farouktouil.personnel_feature.data.local.entities.RemoteKey
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class AppelConsultationRemoteMediator(
    private val appDatabase: AppDatabase,
    private val consultationApiService: ConsultationApiService,
    private val searchQuery: ConsultationSearchQuery
) : RemoteMediator<Int, AppelConsultationEntity>() {

    private val appelConsultationDao = appDatabase.appelConsultationDao()
    private val remoteKeyDao = appDatabase.remoteKeysDao()
    private val searchId = searchQuery.toString()

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
                        // Use the new DAO method
                        remoteKeyDao.getRemoteKeyById(searchId)
                    }
                    remoteKey?.nextKey ?: return MediatorResult.Success(endOfPaginationReached = true)
                }
            }

            val response = consultationApiService.getConsultationCalls(
                page = currentPage,
                nom_appel_consultation = searchQuery.nom_appel_consultation.takeIf { it.isNotBlank() },
                date_depot = searchQuery.date_depot.takeIf { it.isNotBlank() }
            )
            val endOfPaginationReached = response.isEmpty()

            val prevPage = if (currentPage == 1) null else currentPage - 1
            val nextPage = if (endOfPaginationReached) null else currentPage + 1

            appDatabase.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    appelConsultationDao.clearAll()
                    // Use the new DAO method to avoid clearing personnel keys
                    remoteKeyDao.deleteRemoteKeyById(searchId)
                }
                appelConsultationDao.insertAll(response.toEntities())
                // The query parameter is not needed here as per the logic
                remoteKeyDao.insertAll(listOf(RemoteKey(id = searchId, prevKey = prevPage, nextKey = nextPage)))
            }

            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: IOException) {
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            MediatorResult.Error(e)
        }
    }
}
