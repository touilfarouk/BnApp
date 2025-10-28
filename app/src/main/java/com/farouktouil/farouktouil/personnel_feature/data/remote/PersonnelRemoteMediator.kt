package com.farouktouil.farouktouil.personnel_feature.data.remote

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.farouktouil.farouktouil.core.data.local.AppDatabase
import com.farouktouil.farouktouil.personnel_feature.data.local.PersonnelEntity
import com.farouktouil.farouktouil.personnel_feature.data.local.entities.RemoteKey
import com.farouktouil.farouktouil.personnel_feature.data.mapper.toEntities
import com.farouktouil.farouktouil.personnel_feature.domain.model.PersonnelSearchQuery
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class PersonnelRemoteMediator(
    private val searchQuery: PersonnelSearchQuery,
    private val appDatabase: AppDatabase,
    private val personnelApiService: PersonnelApiService
) : RemoteMediator<Int, PersonnelEntity>() {

    private val personnelDao = appDatabase.personnelDao()
    private val remoteKeyDao = appDatabase.remoteKeysDao()

    override suspend fun load(loadType: LoadType, state: PagingState<Int, PersonnelEntity>): MediatorResult {
        return try {
            val page = when (loadType) {
                LoadType.REFRESH -> 1
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val remoteKey = appDatabase.withTransaction {
                        // The id and query are combined to create a unique primary key
                        remoteKeyDao.getRemoteKeys(id = "personnel", query = getQueryString())
                    }
                    if (remoteKey?.nextKey == null) {
                        return MediatorResult.Success(endOfPaginationReached = true)
                    }
                    remoteKey.nextKey
                }
            }

            val response = personnelApiService.getPersonnel(
                page = page,
                name = searchQuery.name,
                structure = searchQuery.structure,
                active = searchQuery.active
            )

            val endOfPaginationReached = response.isEmpty()

            appDatabase.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    personnelDao.clearAll()
                    remoteKeyDao.clearRemoteKeys(getQueryString())
                }
                val prevKey = if (page == 1) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1
                val keys = response.map {
                    // Using a constant id because we only have one query
                    RemoteKey(id = "personnel", prevKey = prevKey, nextKey = nextKey, query = getQueryString())
                }
                remoteKeyDao.insertAll(keys)
                personnelDao.insertAll(response.toEntities())
            }

            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: IOException) {
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            MediatorResult.Error(e)
        }
    }

    private fun getQueryString(): String {
        return "${searchQuery.name}_${searchQuery.structure}_${searchQuery.active}"
    }
}
