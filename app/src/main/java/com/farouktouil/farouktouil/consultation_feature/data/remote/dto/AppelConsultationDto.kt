package com.farouktouil.farouktouil.consultation_feature.data.remote.dto

import com.google.gson.annotations.SerializedName

data class AppelConsultationDto(
    @SerializedName("nom_appel_consultation")
    val nomAppelConsultation: String?,
    
    @SerializedName("date_depot")
    val dateDepot: String?,
    
    @SerializedName("cle_appel_consultation")
    val cleAppelConsultation: String?
) {
    fun toAppelConsultation(id: Int): com.farouktouil.farouktouil.consultation_feature.domain.model.AppelConsultation {
        return com.farouktouil.farouktouil.consultation_feature.domain.model.AppelConsultation(
            id = id,
            nom_appel_consultation = nomAppelConsultation,
            date_depot = dateDepot,
            cle_appel_consultation = cleAppelConsultation
        )
    }
}
