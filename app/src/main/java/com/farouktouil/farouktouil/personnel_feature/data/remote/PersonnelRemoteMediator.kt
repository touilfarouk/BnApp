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
            val queryKey = getQueryString()

            val page = when (loadType) {
                LoadType.REFRESH -> 1
                LoadType.PREPEND -> {
                    val remoteKey = remoteKeyDao.getRemoteKeys(id = REMOTE_KEY_ID, query = queryKey)
                    val prevKey = remoteKey?.prevKey
                    if (prevKey == null) {
                        return MediatorResult.Success(endOfPaginationReached = true)
                    }
                    prevKey
                }
                LoadType.APPEND -> {
                    val remoteKey = remoteKeyDao.getRemoteKeys(id = REMOTE_KEY_ID, query = queryKey)
                    val nextKey = remoteKey?.nextKey
                    if (nextKey == null) {
                        return MediatorResult.Success(endOfPaginationReached = true)
                    }
                    nextKey
                }
            }

            val response = personnelApiService.getPersonnel(
                page = page,
                pageSize = state.config.pageSize,
                search = buildSearchParam()
            )

            val data = response.data
            val pagination = response.pagination
            val endOfPaginationReached = data.isEmpty() || !pagination.hasNext

            appDatabase.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    personnelDao.clearAll()
                    remoteKeyDao.clearRemoteKeys(queryKey)
                }

                val prevKey = if (page <= 1) null else page - 1
                val nextKey = if (pagination.hasNext) page + 1 else null
                val remoteKey = RemoteKey(
                    id = REMOTE_KEY_ID,
                    prevKey = prevKey,
                    nextKey = nextKey,
                    query = queryKey
                )
                remoteKeyDao.insertAll(listOf(remoteKey))
                personnelDao.insertAll(data.toEntities())
            }

            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: IOException) {
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            MediatorResult.Error(e)
        }
    }

    private fun buildSearchParam(): String? {
        val parts = listOfNotNull(
            searchQuery.name?.trim()?.takeIf { it.isNotEmpty() },
            searchQuery.structure?.trim()?.takeIf { it.isNotEmpty() }
        )

        return parts.joinToString(" ").takeIf { it.isNotBlank() }
    }

    private fun getQueryString(): String {
        val nameKey = searchQuery.name?.trim().orEmpty()
        val structureKey = searchQuery.structure?.trim().orEmpty()
        val activeKey = searchQuery.active?.toString().orEmpty()
        return listOf(nameKey, structureKey, activeKey).joinToString(separator = "|")
    }

    companion object {
        private const val REMOTE_KEY_ID = "personnel"
    }
}
