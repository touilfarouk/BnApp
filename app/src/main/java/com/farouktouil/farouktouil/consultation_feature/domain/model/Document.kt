package com.farouktouil.farouktouil.consultation_feature.domain.model

data class Document(
    val fileName: String,
    val fileUrl: String,
    val localFilePath: String? = null,
    val fileSize: Long? = null,
    val lastUpdated: Long = System.currentTimeMillis()
) {
    val isAvailableOffline: Boolean
        get() = !localFilePath.isNullOrBlank()
}