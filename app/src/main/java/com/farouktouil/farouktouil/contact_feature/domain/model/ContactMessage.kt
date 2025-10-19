package com.farouktouil.farouktouil.contact_feature.domain.model

data class ContactMessage(
    val name: String,
    val email: String,
    val phone: String,
    val subject: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis()
)
