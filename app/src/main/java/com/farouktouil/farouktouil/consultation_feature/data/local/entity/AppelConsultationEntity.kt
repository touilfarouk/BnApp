package com.farouktouil.farouktouil.consultation_feature.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

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
    val date_depot: String?,
    val jour_depot: String?,
    val date_fin_evaluation: String?,
    val attribution: String?,
    val num_tender: Int,
    val download_count: Int,
    val code: Int,
    val lastUpdated: Long = System.currentTimeMillis()
) {
    companion object {
        const val DATE_FORMAT = "yyyy-MM-dd"
    }
    
    fun isExpired(expirationTimeInMillis: Long): Boolean {
        return (System.currentTimeMillis() - lastUpdated) > expirationTimeInMillis
    }
    
    val id: Int
        get() = cle_appel_consultation
}
