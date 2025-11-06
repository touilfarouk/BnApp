package com.farouktouil.farouktouil.consultation_feature.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "document_entity",
    foreignKeys = [
        ForeignKey(
            entity = AppelConsultationEntity::class,
            parentColumns = ["cle_appel_consultation"],
            childColumns = ["consultationId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["consultationId"], name = "document_consultation_id_idx")
    ]
)
data class DocumentEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val consultationId: Int,
    val year: String,
    val fileName: String,
    val fileUrl: String
)
