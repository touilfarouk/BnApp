package com.farouktouil.farouktouil.consultation_feature.domain.model

data class AppelConsultation(
    val id: Int,
    val nom_appel_consultation: String?,
    val date_depot: String?,
    val cle_appel_consultation: String?
)
