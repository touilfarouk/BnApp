package com.farouktouil.farouktouil.consultation_feature.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.farouktouil.farouktouil.consultation_feature.domain.model.AppelConsultation
import java.io.File

@Entity(
    tableName = "document_entity",
    foreignKeys = [
        ForeignKey(
            entity = AppelConsultationEntity::class,
            parentColumns = ["id"],
            childColumns = ["consultationId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["consultationId"], name = "document_consultation_id_idx"),
        Index(value = ["consultationId", "fileUrl"], unique = true)
    ]
)
data class DocumentEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val consultationId: Int,
    val year: String,
    val fileName: String,
    val fileUrl: String,
    val localFilePath: String? = null,
    val fileSize: Long? = null,
    val lastUpdated: Long = System.currentTimeMillis()
) {
    fun toDomain(): AppelConsultation.Document {
        return AppelConsultation.Document(
            year = year,
            fileName = fileName,
            fileUrl = fileUrl,
            localFilePath = localFilePath.takeIf { !it.isNullOrBlank() && File(it).exists() },
            fileSize = fileSize,
            lastUpdated = lastUpdated
        )
    }
}
