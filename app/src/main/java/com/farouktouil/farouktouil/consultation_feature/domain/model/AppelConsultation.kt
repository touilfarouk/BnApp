package com.farouktouil.farouktouil.consultation_feature.domain.model

data class AppelConsultation(
    val id: Int,
    val title: String,
    val depositDate: String,
    val dayOfWeek: String,
    val tenderNumber: Int,
    val documents: List<Document>
) {
    data class Document(
        val year: String,
        val fileName: String,
        val fileUrl: String,
        val localFilePath: String? = null,
        val fileSize: Long? = null,
        val lastUpdated: Long = System.currentTimeMillis()
    ) {
        val isAvailableOffline: Boolean
            get() = !localFilePath.isNullOrBlank()
    }
}