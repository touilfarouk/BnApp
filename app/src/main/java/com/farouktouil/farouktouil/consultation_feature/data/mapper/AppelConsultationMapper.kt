package com.farouktouil.farouktouil.consultation_feature.data.mapper

import com.farouktouil.farouktouil.consultation_feature.data.local.entity.AppelConsultationEntity
import com.farouktouil.farouktouil.consultation_feature.data.remote.dto.AppelConsultationDto
import com.farouktouil.farouktouil.consultation_feature.domain.model.AppelConsultation

typealias Document = AppelConsultation.Document

fun AppelConsultationDto.toDomain(documents: List<Document> = emptyList()): AppelConsultation {
    return AppelConsultation(
        id = id,
        title = title ?: "",
        depositDate = depositDate ?: "",
        dayOfWeek = dayOfWeek ?: "",
        tenderNumber = tenderNumber,
        documents = documents
    )
}

fun List<AppelConsultationDto>.toDomainList(documentsMap: Map<Int, List<Document>> = emptyMap()): List<AppelConsultation> {
    return this.map { it.toDomain(documentsMap[it.id] ?: emptyList()) }
}

fun AppelConsultation.toEntity(): AppelConsultationEntity {
    return AppelConsultationEntity(
        id = id,
        title = title,
        depositDate = depositDate,
        dayOfWeek = dayOfWeek,
        tenderNumber = tenderNumber
    )
}

fun List<AppelConsultation>.toEntities(): List<AppelConsultationEntity> {
    return this.map { it.toEntity() }
}
