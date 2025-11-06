package com.farouktouil.farouktouil.consultation_feature.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class AppelConsultationWithDocuments(
    @Embedded val consultation: AppelConsultationEntity,
    @Relation(
        parentColumn = "cle_appel_consultation",
        entityColumn = "consultationId"
    )
    val documents: List<DocumentEntity> = emptyList()
)
