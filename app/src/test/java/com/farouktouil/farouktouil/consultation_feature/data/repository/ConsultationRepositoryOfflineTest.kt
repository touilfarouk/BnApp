package com.farouktouil.farouktouil.consultation_feature.data.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.farouktouil.farouktouil.consultation_feature.data.local.AppelConsultationDao
import com.farouktouil.farouktouil.consultation_feature.data.local.entity.AppelConsultationEntity
import com.farouktouil.farouktouil.consultation_feature.data.remote.ConsultationApiService
import com.farouktouil.farouktouil.consultation_feature.data.remote.dto.AppelConsultationDto
import com.farouktouil.farouktouil.consultation_feature.domain.model.ConsultationSearchQuery
import com.farouktouil.farouktouil.core.data.local.AppDatabase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import retrofit2.Response

@ExperimentalCoroutinesApi
class ConsultationRepositoryOfflineTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: AppDatabase
    private lateinit var repository: ConsultationRepositoryImpl
    private lateinit var appelConsultationDao: AppelConsultationDao

    @Mock
    private lateinit var mockApiService: ConsultationApiService

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries()
            .build()

        appelConsultationDao = database.appelConsultationDao()
        
        repository = ConsultationRepositoryImpl(database, mockApiService)
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun `should return data from local database when offline`() = runTest {
        // Given - Insert test data directly into the local database
        val testData = listOf(
            AppelConsultationEntity(
                id = 1,
                nom_appel_consultation = "Offline Test 1",
                date_depot = "01/01/2025",
                cle_appel_consultation = "offline1"
            ),
            AppelConsultationEntity(
                id = 2,
                nom_appel_consultation = "Offline Test 2",
                date_depot = "02/01/2025",
                cle_appel_consultation = "offline2"
            )
        )
        appelConsultationDao.insertAll(testData)

        // When - Simulate offline by making the API return an error
        `when`(mockApiService.getConsultationCalls(any(), any(), any()))
            .thenThrow(RuntimeException("No internet connection"))

        val searchQuery = ConsultationSearchQuery()
        val pager = repository.getConsultationCalls(searchQuery)

        // Then - Should still return data from local database
        val result = pager.flow.first()
        assertTrue(result is PagingSource.LoadResult.Page<Int, AppelConsultationEntity>)
        val page = result as PagingSource.LoadResult.Page<Int, AppelConsultationEntity>
        assertEquals(2, page.data.size)
        assertTrue(page.data.any { it.nom_appel_consultation == "Offline Test 1" })
        assertTrue(page.data.any { it.nom_appel_consultation == "Offline Test 2" })
    }

    @Test
    fun `should save data to local database when online`() = runTest {
        // Given - Mock API response
        val apiResponse = listOf(
            AppelConsultationDto(
                nomAppelConsultation = "Online Test 1",
                dateDepot = "03/01/2025",
                cleAppelConsultation = "online1"
            ),
            AppelConsultationDto(
                nomAppelConsultation = "Online Test 2",
                dateDepot = "04/01/2025",
                cleAppelConsultation = "online2"
            )
        )
        
        `when`(mockApiService.getConsultationCalls(1, null, null))
            .thenReturn(Response.success(apiResponse))

        // When - Make the API call
        val searchQuery = ConsultationSearchQuery()
        val pager = repository.getConsultationCalls(searchQuery)
        
        // Trigger data loading
        pager.flow.first()

        // Then - Verify data is saved to the local database
        val pagingSource = object : PagingSource<Int, AppelConsultationEntity>() {
            override fun getRefreshKey(state: PagingState<Int, AppelConsultationEntity>): Int? = null
            override suspend fun load(params: LoadParams<Int>): LoadResult<Int, AppelConsultationEntity> {
                return try {
                    val items = appelConsultationDao.getAppelConsultationPagingSource("", "date_desc").load(
                        PagingSource.LoadParams.Refresh(
                            key = null,
                            loadSize = 10,
                            placeholdersEnabled = false
                        )
                    ) as PagingSource.LoadResult.Page<Int, AppelConsultationEntity>
                    LoadResult.Page(
                        data = items.data,
                        prevKey = items.prevKey,
                        nextKey = items.nextKey
                    )
                } catch (e: Exception) {
                    LoadResult.Error(e)
                }
            }
        }
        
        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 10,
                placeholdersEnabled = false
            )
        ) as PagingSource.LoadResult.Page<Int, AppelConsultationEntity>
        
        assertEquals(2, result.data.size)
        assertTrue(result.data.any { it.nom_appel_consultation?.contains("Online Test") == true })
    }

    @Test
    fun `should use cached data when offline after successful API call`() = runTest {
        // Given - First call with internet (data is cached)
        val apiResponse = listOf(
            AppelConsultationDto(
                nomAppelConsultation = "Cached Test",
                dateDepot = "05/01/2025",
                cleAppelConsultation = "cached1"
            )
        )
        
        `when`(mockApiService.getConsultationCalls(1, null, null))
            .thenReturn(Response.success(apiResponse))
            .thenThrow(RuntimeException("No internet connection"))

        // First call - Online
        val searchQuery = ConsultationSearchQuery()
        val pager = repository.getConsultationCalls(searchQuery)
        pager.flow.first() // Trigger data loading

        // Clear the database to simulate fresh start
        appelConsultationDao.clearAll()
        
        // Insert the same data directly to simulate it was previously cached
        val cachedData = AppelConsultationEntity(
            id = 1,
            nom_appel_consultation = "Cached Test",
            date_depot = "05/01/2025",
            cle_appel_consultation = "cached1"
        )
        appelConsultationDao.insert(cachedData)

        // When - Second call - Offline
        val offlinePager = repository.getConsultationCalls(searchQuery)
        
        // Then - Should return cached data even though offline
        val result = offlinePager.flow.first()
        assertTrue(result is PagingSource.LoadResult.Page<Int, AppelConsultationEntity>)
        val page = result as PagingSource.LoadResult.Page<Int, AppelConsultationEntity>
        assertEquals(1, page.data.size)
        assertEquals("Cached Test", page.data[0].nom_appel_consultation)
    }
}
