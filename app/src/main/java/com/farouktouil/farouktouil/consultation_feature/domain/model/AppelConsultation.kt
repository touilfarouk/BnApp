package com.farouktouil.farouktouil.consultation_feature.domain.model

data class AppelConsultation(
    val id: Int,
    val nom_appel_consultation: String,
    val date_depot: String? = null,
    val cle_appel_consultation: String,
    val jour_depot: String? = null,
    val date_fin_evaluation: String? = null,
    val attribution: String? = null,
    val num_tender: Int = 0,
    val download_count: Int = 0,
    val code: Int = 0
) {
    val displayDate: String
        get() = date_depot ?: ""
        
    val displayTitle: String
        get() = nom_appel_consultation.ifEmpty { "Sans titre" }
        
    val displayKey: String
        get() = "#${cle_appel_consultation}"
}
