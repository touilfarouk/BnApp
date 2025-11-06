package com.farouktouil.farouktouil.consultation_feature.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.farouktouil.farouktouil.consultation_feature.domain.model.AppelConsultation

@Entity(
    tableName = "appel_consultation",
    indices = [
        Index(value = ["nom_appel_consultation"]),
        Index(value = ["date_depot"]),
        Index(value = ["cle_appel_consultation"], unique = true)
    ]
)
data class AppelConsultationEntity(
    @PrimaryKey
    val cle_appel_consultation: Int,
    val nom_appel_consultation: String,
    val date_depot: String? = null,
    val jour_depot: String? = null,
    val date_fin_evaluation: String? = null,
    val attribution: String? = null,
    val num_tender: String = "",
    val download_count: Int = 0,
    val code: String = "",
    val lastUpdated: Long = System.currentTimeMillis()
) {
    companion object {
        const val DATE_FORMAT = "yyyy-MM-dd"
        
        fun fromDomain(domain: AppelConsultation): AppelConsultationEntity {
            return AppelConsultationEntity(
                cle_appel_consultation = domain.cle_appel_consultation.toIntOrNull() ?: 0,
                nom_appel_consultation = domain.nom_appel_consultation,
                date_depot = domain.date_depot,
                jour_depot = domain.jour_depot,
                date_fin_evaluation = domain.date_fin_evaluation,
                attribution = domain.attribution,
                num_tender = if (domain.num_tender.isEmpty()) "0" else domain.num_tender,
                download_count = domain.download_count,
                code = if (domain.code.isEmpty()) "0" else domain.code
            )
        }
    }
    
    fun toDomain(): AppelConsultation {
        return AppelConsultation(
            id = cle_appel_consultation,
            nom_appel_consultation = nom_appel_consultation,
            date_depot = date_depot,
            cle_appel_consultation = cle_appel_consultation.toString(),
            jour_depot = jour_depot,
            date_fin_evaluation = date_fin_evaluation,
            attribution = attribution,
            num_tender = num_tender,
            download_count = download_count,
            code = code
        )
    }
    
    fun isExpired(expirationTimeInMillis: Long): Boolean {
        return (System.currentTimeMillis() - lastUpdated) > expirationTimeInMillis
    }
    
    val id: Int
        get() = cle_appel_consultation
}
