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
import com.farouktouil.farouktouil.core.util.NetworkUtils
import retrofit2.HttpException
import java.io.IOException
import java.net.UnknownHostException
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class AppelConsultationRemoteMediator @Inject constructor(
    private val appDatabase: AppDatabase,
    private val consultationApiService: ConsultationApiService,
    private val searchQuery: ConsultationSearchQuery,
    private val networkUtils: NetworkUtils
) : RemoteMediator<Int, AppelConsultationEntity>() {

    private val appelConsultationDao: AppelConsultationDao = appDatabase.appelConsultationDao()
    private val remoteKeyDao: RemoteKeysDao = appDatabase.consultationRemoteKeysDao()
    
    private val searchQueryText = searchQuery.nom_appel_consultation
    
    // Get count of items in database
    private suspend fun getCachedItemCount(): Int {
        return try {
            appelConsultationDao.getTotalCount()
        } catch (e: Exception) {
            Log.e("RemoteMediator", "Error getting item count", e)
            0
        }
    }
    
    override suspend fun initialize(): InitializeAction {
        return if (getCachedItemCount() == 0) {
            Log.d("RemoteMediator", "No cached data, launching initial refresh")
            InitializeAction.LAUNCH_INITIAL_REFRESH
        } else {
            Log.d("RemoteMediator", "Using cached data")
            InitializeAction.SKIP_INITIAL_REFRESH
        }
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, AppelConsultationEntity>
    ): MediatorResult {
        Log.d("RemoteMediator", "Load triggered - Type: $loadType, Query: '$searchQueryText'")
        
        // If we're offline, return success to show cached data
        if (!networkUtils.isNetworkAvailable()) {
            Log.d("RemoteMediator", "Offline mode - returning cached data")
            // If we have no items in the database, return error to show error state
            val itemCount = getCachedItemCount()
            if (itemCount == 0) {
                return MediatorResult.Error(IOException("No network connection and no cached data"))
            }
            return MediatorResult.Success(endOfPaginationReached = true)
        }
        
        return try {
            // Get the page to fetch
            val page = when (loadType) {
                LoadType.REFRESH -> {
                    Log.d("RemoteMediator", "REFRESH - Getting first page")
                    1
                }
                LoadType.PREPEND -> {
                    Log.d("RemoteMediator", "PREPEND - Not supported, returning success")
                    return MediatorResult.Success(endOfPaginationReached = true)
                }
                LoadType.APPEND -> {
                    Log.d("RemoteMediator", "APPEND - Getting next page")
                    val remoteKey = getRemoteKeyForLastItem(state)
                    if (remoteKey?.nextKey == null) {
                        Log.d("RemoteMediator", "No more pages to load")
                        return MediatorResult.Success(endOfPaginationReached = true)
                    }
                    remoteKey.nextKey
                }
            }

            // Fetch data from the API
            Log.d("RemoteMediator", "Fetching page $page from API")
            val response = try {
                consultationApiService.getConsultationCalls(
                    page = page,
                    search = searchQuery.nom_appel_consultation
                )
            } catch (e: Exception) {
                Log.e("RemoteMediator", "Error fetching from API: ${e.message}", e)
                return MediatorResult.Error(e)
            }

            if (!response.isSuccessful) {
                Log.e("RemoteMediator", "API error: ${response.code()} - ${response.message()}")
                return MediatorResult.Error(HttpException(response))
            }

            val responseBody = response.body()
            if (responseBody == null) {
                Log.e("RemoteMediator", "Response body is null")
                return MediatorResult.Error(NullPointerException("Response body is null"))
            }

            val consultations = responseBody.data
            Log.d("RemoteMediator", "Fetched ${consultations.size} items from API")

            val endOfPaginationReached = consultations.isEmpty()
            
            // Log the first consultation if available
            if (consultations.isNotEmpty()) {
                val first = consultations.first()
                Log.d("RemoteMediator", "First consultation in response: ${first.id} - ${first.title}")
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
                val keys = consultations.map { consultation ->
                    RemoteKey(
                        consultationId = consultation.id,
                        prevKey = prevKey,
                        nextKey = nextKey,
                        query = searchQuery.nom_appel_consultation
                    )
                }
                
                if (keys.isNotEmpty()) {
                    remoteKeyDao.insertAll(keys)
                    
                    // Save consultations to database
                    val entities = consultations.map { it.toEntity() }
                    Log.d("RemoteMediator", "Inserting ${entities.size} entities into database")
                    appelConsultationDao.insertAll(entities)
                }
            }

            Log.d("RemoteMediator", "Load completed successfully")
            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: IOException) {
            if (e is UnknownHostException) {
                Log.d("RemoteMediator", "No network connection - using cached data")
                MediatorResult.Success(endOfPaginationReached = true)
            } else {
                Log.e("RemoteMediator", "Network error: ${e.message}", e)
                MediatorResult.Error(e)
            }
        } catch (e: HttpException) {
            Log.e("RemoteMediator", "HTTP error: ${e.message}", e)
            MediatorResult.Success(endOfPaginationReached = true) // Return success to show cached data
        } catch (e: Exception) {
            Log.e("RemoteMediator", "Unexpected error: ${e.message}", e)
            MediatorResult.Error(e)
        }
    }

    /**
     * Get the remote key for the last item in the paging state.
     */
    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, AppelConsultationEntity>): RemoteKey? {
        // Get the last page that was retrieved, that contained items.
        // From that last page, get the last item
        return state.lastItemOrNull()?.let { appelConsultation ->
            // Get the remote keys of the last item retrieved
            remoteKeyDao.remoteKeysByConsultationId(appelConsultation.cle_appel_consultation)
        } ?: remoteKeyDao.getLastRemoteKey(searchQuery.nom_appel_consultation)
    }
}