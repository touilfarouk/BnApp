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
        val fileName: String,
        val fileUrl: String
    )
}