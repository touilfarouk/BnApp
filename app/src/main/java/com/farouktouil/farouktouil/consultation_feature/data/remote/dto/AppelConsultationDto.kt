package com.farouktouil.farouktouil.consultation_feature.data.remote.dto

import com.farouktouil.farouktouil.consultation_feature.data.local.entity.AppelConsultationEntity
import com.farouktouil.farouktouil.consultation_feature.domain.model.AppelConsultation
import com.google.gson.annotations.SerializedName

data class AppelConsultationDto(
    @SerializedName("nom_appel_consultation")
    val nomAppelConsultation: String?,
    
    @SerializedName("date_depot")
    val dateDepot: String?,
    
    @SerializedName("cle_appel_consultation")
    val cleAppelConsultation: Int,
    
    @SerializedName("jour_depot")
    val jourDepot: String? = null,
    
    @SerializedName("date_fin_evaluation")
    val dateFinEvaluation: String? = null,
    
    @SerializedName("attribution")
    val attribution: String? = null,
    
    @SerializedName("num_tender")
    val numTender: Int = 0,
    
    @SerializedName("download_count")
    val downloadCount: Int = 0,
    
    @SerializedName("code")
    val code: Int = 0
) {
    fun toAppelConsultation(): AppelConsultation {
        return AppelConsultation(
            id = cleAppelConsultation,
            nom_appel_consultation = nomAppelConsultation ?: "",
            date_depot = dateDepot,
            cle_appel_consultation = cleAppelConsultation.toString(),
            jour_depot = jourDepot,
            date_fin_evaluation = dateFinEvaluation,
            attribution = attribution,
            num_tender = numTender,
            download_count = downloadCount,
            code = code
        )
    }

    fun toEntity(): AppelConsultationEntity {
        return AppelConsultationEntity(
            cle_appel_consultation = cleAppelConsultation,
            nom_appel_consultation = nomAppelConsultation ?: "",
            date_depot = dateDepot,
            jour_depot = jourDepot,
            date_fin_evaluation = dateFinEvaluation,
            attribution = attribution,
            num_tender = numTender,
            download_count = downloadCount,
            code = code
        )
    }
}
