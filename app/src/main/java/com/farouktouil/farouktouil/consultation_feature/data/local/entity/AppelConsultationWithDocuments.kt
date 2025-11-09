package com.farouktouil.farouktouil.consultation_feature.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation
import com.farouktouil.farouktouil.consultation_feature.domain.model.AppelConsultation
import com.farouktouil.farouktouil.consultation_feature.data.local.entity.AppelConsultationEntity
import com.farouktouil.farouktouil.consultation_feature.data.local.entity.DocumentEntity

data class AppelConsultationWithDocuments(
    @Embedded val consultation: AppelConsultationEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "consultationId"
    )
    val documents: List<DocumentEntity> = emptyList()
) {
    fun toDomain(): AppelConsultation {
        return consultation.toDomain(
            documents = documents.map { it.toDomain() }
        )
    }
}
