package com.farouktouil.farouktouil.consultation_feature.data.remote.dto

import com.farouktouil.farouktouil.consultation_feature.data.local.entity.AppelConsultationEntity
import com.farouktouil.farouktouil.consultation_feature.domain.model.AppelConsultation
import com.google.gson.annotations.SerializedName

data class DocumentDto(
    @SerializedName("year")
    val year: String,
    
    @SerializedName("fileName")
    val fileName: String,
    
    @SerializedName("fileUrl")
    val fileUrl: String
)

data class AppelConsultationDto(
    @SerializedName("id")
    val id: Int,
    
    @SerializedName("title")
    val title: String,
    
    @SerializedName("depositDate")
    val depositDate: String?,
    
    @SerializedName("dayOfWeek")
    val dayOfWeek: String? = null,
    
    @SerializedName("tenderNumber")
    val tenderNumber: Int = 0,
    
    @SerializedName("documents")
    val documents: List<DocumentDto> = emptyList()
) {
    fun toDomain(): AppelConsultation {
        return AppelConsultation(
            id = id,
            title = title,
            depositDate = depositDate ?: "",
            dayOfWeek = dayOfWeek ?: "",
            tenderNumber = tenderNumber,
            documents = documents.map { 
                AppelConsultation.Document(
                    year = it.year,
                    fileName = it.fileName,
                    fileUrl = it.fileUrl
                )
            }
        )
    }

    fun toEntity(): AppelConsultationEntity {
        return AppelConsultationEntity(
            id = id,
            title = title,
            depositDate = depositDate ?: "",
            dayOfWeek = dayOfWeek ?: "",
            tenderNumber = tenderNumber
        )
    }
}
