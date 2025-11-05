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
    
    @SerializedName("evaluationEndDate")
    val evaluationEndDate: String? = null,
    
    @SerializedName("attribution")
    val attribution: String? = null,
    
    @SerializedName("tenderNumber")
    val tenderNumber: Int = 0,
    
    @SerializedName("downloadCount")
    val downloadCount: Int = 0,
    
    @SerializedName("code")
    val code: Int = 0,
    
    @SerializedName("documents")
    val documents: List<DocumentDto> = emptyList()
) {
    fun toAppelConsultation(): AppelConsultation {
        return AppelConsultation(
            id = id,
            nom_appel_consultation = title,
            date_depot = depositDate,
            cle_appel_consultation = id.toString(),
            jour_depot = dayOfWeek,
            date_fin_evaluation = evaluationEndDate,
            attribution = attribution,
            num_tender = tenderNumber,
            download_count = downloadCount,
            code = code
        )
    }

    fun toEntity(): AppelConsultationEntity {
        return AppelConsultationEntity(
            cle_appel_consultation = id,
            nom_appel_consultation = title,
            date_depot = depositDate,
            jour_depot = dayOfWeek,
            date_fin_evaluation = evaluationEndDate,
            attribution = attribution,
            num_tender = tenderNumber,
            download_count = downloadCount,
            code = code
        )
    }
}
