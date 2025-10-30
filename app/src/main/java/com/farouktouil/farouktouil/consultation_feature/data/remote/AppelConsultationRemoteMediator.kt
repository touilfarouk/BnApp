package com.farouktouil.farouktouil.consultation_feature.data.remote

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.farouktouil.farouktouil.consultation_feature.data.local.dao.AppelConsultationDao
import com.farouktouil.farouktouil.consultation_feature.data.local.dao.RemoteKeysDao
import com.farouktouil.farouktouil.consultation_feature.data.local.entity.AppelConsultationEntity
import com.farouktouil.farouktouil.consultation_feature.data.local.entity.RemoteKey
import com.farouktouil.farouktouil.consultation_feature.data.mapper.toEntity
import com.farouktouil.farouktouil.consultation_feature.domain.model.ConsultationSearchQuery
import com.farouktouil.farouktouil.core.data.local.AppDatabase
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class AppelConsultationRemoteMediator @Inject constructor(
    private val appDatabase: AppDatabase,
    private val consultationApiService: ConsultationApiService,
    private val searchQuery: ConsultationSearchQuery
) : RemoteMediator<Int, AppelConsultationEntity>() {

    private val appelConsultationDao: AppelConsultationDao = appDatabase.appelConsultationDao()
    private val remoteKeyDao: RemoteKeysDao = appDatabase.consultationRemoteKeysDao()
    
    private val query = searchQuery.nom_appel_consultation ?: ""
    
    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, AppelConsultationEntity>
    ): MediatorResult {
        Log.d("RemoteMediator", "Load triggered - Type: $loadType")
        
        return try {
            // Get the page to fetch
            val page = when (loadType) {
                LoadType.REFRESH -> {
                    Log.d("RemoteMediator", "REFRESH - Getting first page")
                    val remoteKeys = getRemoteKeyForLastItem(state)
                    remoteKeys?.nextKey?.minus(1) ?: 1
                }
                LoadType.PREPEND -> {
                    Log.d("RemoteMediator", "PREPEND - Not supported, returning success")
                    return MediatorResult.Success(endOfPaginationReached = true)
                }
                LoadType.APPEND -> {
                    Log.d("RemoteMediator", "APPEND - Getting next page")
                    val remoteKeys = getRemoteKeyForLastItem(state)
                        ?: return MediatorResult.Success(endOfPaginationReached = true)
                    remoteKeys.nextKey ?: return MediatorResult.Success(endOfPaginationReached = true)
                }      }

            // Fetch data from the API
            Log.d("RemoteMediator", "Fetching page $page from API")
            val response = try {
                consultationApiService.getConsultationCalls(
                    page = page,
                    nom_appel_consultation = searchQuery.nom_appel_consultation,
                    date_depot = searchQuery.date_depot,
                    sort = "cle_appel_consultation",
                    order = "DESC"
                )
            } catch (e: Exception) {
                Log.e("RemoteMediator", "Error fetching from API: ${e.message}")
                throw e
            }

            if (!response.isSuccessful) {
                return MediatorResult.Error(HttpException(response))
            }

            val consultations = response.body() ?: emptyList()
            val endOfPaginationReached = consultations.isEmpty()
            
            // Log the first consultation if available
            if (consultations.isNotEmpty()) {
                val first = consultations.first()
                Log.d("RemoteMediator", "First consultation in response: ${first.cleAppelConsultation} - ${first.nomAppelConsultation}")
            } else {
                Log.d("RemoteMediator", "No consultations in response")
            }

            // Save data to the database
            Log.d("RemoteMediator", "Saving ${consultations.size} items to database")
            appDatabase.withTransaction {
                // Clear all data if this is a refresh
                if (loadType == LoadType.REFRESH) {
                    Log.d("RemoteMediator", "Clearing existing data for refresh")
                    remoteKeyDao.clearRemoteKeys()
                    appelConsultationDao.clearAll()
                }

                // Calculate next and prev keys
                val prevKey = if (page == 1) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1
                
                Log.d("RemoteMediator", "Page: $page, Prev: $prevKey, Next: $nextKey, End: $endOfPaginationReached")

                // Save the remote key for pagination
                val remoteKey = RemoteKey(
                    id = "consultation_${searchQuery.nom_appel_consultation ?: "all"}",
                    prevKey = prevKey,
                    nextKey = nextKey,
                    query = query
                )
                remoteKeyDao.insertAll(listOf(remoteKey))

                // Save consultations to database
                val entities = consultations.map { it.toEntity() }
                Log.d("RemoteMediator", "Inserting ${entities.size} entities into database")
                appelConsultationDao.insertAll(entities)
            }

            Log.d("RemoteMediator", "Load completed successfully")
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: IOException) {
            Log.e("RemoteMediator", "Network error: ${e.message}", e)
            return MediatorResult.Error(e)
        } catch (e: HttpException) {
            Log.e("RemoteMediator", "HTTP error: ${e.code()} - ${e.message()}", e)
            return MediatorResult.Error(e)
        } catch (e: Exception) {
            Log.e("RemoteMediator", "Unexpected error: ${e.message}", e)
            return MediatorResult.Error(e)
        }
    }

    /**
     * Get the remote key for the last item in the paging state.
     */
    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, AppelConsultationEntity>): RemoteKey? {
        val id = "consultation_${searchQuery.nom_appel_consultation ?: "all"}"
        return remoteKeyDao.getRemoteKeys(id, searchQuery.nom_appel_consultation)
    }
}
