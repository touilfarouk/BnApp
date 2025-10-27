package com.farouktouil.farouktouil.consultation_feature.domain.model

data class AppelConsultation(
    val nom_appel_consultation: String?,
    val date_depot: String?,
    val cle_appel_consultation: String?
) {
    val displayTitle: String
        get() = nom_appel_consultation?.trim() ?: "N/A"

    val displayDate: String
        get() = date_depot?.trim() ?: "N/A"

    val displayKey: String
        get() = cle_appel_consultation?.trim() ?: "N/A"
}
