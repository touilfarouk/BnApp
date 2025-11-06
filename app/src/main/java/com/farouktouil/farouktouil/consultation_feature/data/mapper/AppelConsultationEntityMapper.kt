package com.farouktouil.farouktouil.consultation_feature.data.mapper

import com.farouktouil.farouktouil.consultation_feature.data.local.entity.AppelConsultationEntity
import com.farouktouil.farouktouil.consultation_feature.domain.model.AppelConsultation

fun AppelConsultationEntity.toDomain(documents: List<AppelConsultation.Document> = emptyList()): AppelConsultation {
    return AppelConsultation(
        id = id,
        title = title,
        depositDate = depositDate,
        dayOfWeek = dayOfWeek,
        tenderNumber = tenderNumber,
        documents = documents
    )
}

fun List<AppelConsultationEntity>.toDomainList(documentsMap: Map<Int, List<AppelConsultation.Document>> = emptyMap()): List<AppelConsultation> {
    return this.map { it.toDomain(documentsMap[it.id] ?: emptyList()) }
}
