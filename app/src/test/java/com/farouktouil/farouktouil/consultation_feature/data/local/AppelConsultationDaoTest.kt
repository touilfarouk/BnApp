package com.farouktouil.farouktouil.consultation_feature.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.farouktouil.farouktouil.consultation_feature.data.local.entity.AppelConsultationEntity
import com.farouktouil.farouktouil.core.data.local.AppDatabase
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AppelConsultationDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var dao: com.farouktouil.farouktouil.consultation_feature.data.local.dao.AppelConsultationDao

    @Before
    fun setup() {
        // Using an in-memory database for testing
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries()
            .build()

        dao = database.appelConsultationDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun insertAndRetrieveConsultation() = runBlocking {
        // Given
        val testItem = AppelConsultationEntity(
            id = 1,
            nom_appel_consultation = "Test Consultation",
            date_depot = "29/10/2025",
            cle_appel_consultation = "test123"
        )

        // When
        dao.insert(testItem)
        val result = dao.getAppelConsultationById(1)

        // Then
        assertNotNull(result)
        assertEquals("Test Consultation", result?.nom_appel_consultation)
        assertEquals("29/10/2025", result?.date_depot)
    }

    @Test
    fun searchConsultation() = runBlocking {
        // Given
        val items = listOf(
            AppelConsultationEntity(
                id = 1,
                nom_appel_consultation = "Consultation A",
                date_depot = "01/01/2025",
                cle_appel_consultation = "key1"
            ),
            AppelConsultationEntity(
                id = 2,
                nom_appel_consultation = "Consultation B",
                date_depot = "02/01/2025",
                cle_appel_consultation = "key2"
            )
        )
        dao.insertAll(items)

        // When
        val searchResults = dao.getAppelConsultationPagingSource("A", dateQuery).load(
            androidx.paging.PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 10,
                placeholdersEnabled = false
            )
        )

        // Then
        val result = (searchResults as androidx.paging.PagingSource.LoadResult.Page).data
        assertEquals(1, result.size)
        assertEquals("Consultation A", result[0].nom_appel_consultation)
    }

    @Test
    fun deleteConsultation() = runBlocking {
        // Given
        val testItem = AppelConsultationEntity(
            id = 1,
            nom_appel_consultation = "To be deleted",
            date_depot = "01/01/2025",
            cle_appel_consultation = "delete_me"
        )
        dao.insert(testItem)

        // When
        dao.deleteById(1)
        val result = dao.getAppelConsultationById(1)

        // Then
        assertNull(result)
    }

    @Test
    fun getAllConsultations() = runBlocking {
        // Given
        val items = listOf(
            AppelConsultationEntity(
                id = 1,
                nom_appel_consultation = "Item 1",
                date_depot = "01/01/2025",
                cle_appel_consultation = "key1"
            ),
            AppelConsultationEntity(
                id = 2,
                nom_appel_consultation = "Item 2",
                date_depot = "02/01/2025",
                cle_appel_consultation = "key2"
            )
        )
        dao.insertAll(items)

        // When
        val allItems = dao.getAppelConsultationPagingSource(dateQuery = dateQuery).load(
            androidx.paging.PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 10,
                placeholdersEnabled = false
            )
        )

        // Then
        val result = (allItems as androidx.paging.PagingSource.LoadResult.Page).data
        assertEquals(2, result.size)
    }

    @Test
    fun testIsExpired() = runBlocking {
        // Given
        val oldItem = AppelConsultationEntity(
            id = 1,
            nom_appel_consultation = "Old Item",
            date_depot = "01/01/2020",
            cle_appel_consultation = "old",
            lastUpdated = System.currentTimeMillis() - (25 * 60 * 60 * 1000) // 25 hours ago
        )

        // When
        val isExpired = oldItem.isExpired(24 * 60 * 60 * 1000) // 24 hours expiration

        // Then
        assertTrue(isExpired)
    }
}
