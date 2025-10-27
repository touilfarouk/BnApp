package com.farouktouil.farouktouil.contact_feature.domain.repository

import com.farouktouil.farouktouil.contact_feature.domain.model.ContactMessage
import com.farouktouil.farouktouil.contact_feature.domain.model.Personnel

interface ContactRepository {

    suspend fun submitContactMessage(contactMessage: ContactMessage): Result<Unit>

    suspend fun getContactInfo(): ContactInfo

    suspend fun getPersonnel(): Result<List<Personnel>>
}

data class ContactInfo(
    val address: String = "123 Business St, City, Country",
    val phone: String = "+1 (555) 123-4567",
    val email: String = "contact@orderapp.com",
    val hours: String = "Mon-Fri 9:00 AM - 6:00 PM"
)
