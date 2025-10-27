package com.farouktouil.farouktouil.contact_feature.data.repository

import android.util.Log
import com.farouktouil.farouktouil.contact_feature.domain.model.ContactMessage
import com.farouktouil.farouktouil.contact_feature.domain.model.Personnel
import com.farouktouil.farouktouil.contact_feature.domain.repository.ContactInfo
import com.farouktouil.farouktouil.contact_feature.domain.repository.ContactRepository
import com.farouktouil.farouktouil.contact_feature.data.remote.PersonnelRemoteDataSource

class ContactRepositoryImpl(
    private val personnelRemoteDataSource: PersonnelRemoteDataSource
) : ContactRepository {

    override suspend fun submitContactMessage(contactMessage: ContactMessage): Result<Unit> {
        return try {
            // Here you would typically send the message to a server or save it locally
            // For now, we'll just log it and simulate success
            Log.d("ContactRepository", "Contact message submitted: $contactMessage")

            // Simulate network delay
            kotlinx.coroutines.delay(1000)

            // In a real app, you would make an API call here
            // For example:
            // val response = apiService.submitContactMessage(contactMessage)
            // if (response.isSuccessful) {
            //     Result.success(Unit)
            // } else {
            //     Result.failure(Exception("Failed to submit message"))
            // }

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("ContactRepository", "Error submitting contact message", e)
            Result.failure(e)
        }
    }

    override suspend fun getContactInfo(): ContactInfo {
        return ContactInfo()
    }

    override suspend fun getPersonnel(): Result<List<Personnel>> {
        return try {
            val personnel = personnelRemoteDataSource.getPersonnel()
            Log.d("ContactRepository", "Personnel fetched successfully: ${personnel.size} items")
            Result.success(personnel)
        } catch (e: Exception) {
            Log.e("ContactRepository", "Error fetching personnel", e)
            Result.failure(e)
        }
    }
}
