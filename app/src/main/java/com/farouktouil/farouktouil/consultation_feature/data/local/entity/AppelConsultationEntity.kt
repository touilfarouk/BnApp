package com.farouktouil.farouktouil.consultation_feature.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "appel_consultation",
    indices = [
        Index(value = ["nom_appel_consultation"], name = "idx_appel_consultation_name"),
        Index(value = ["date_depot"], name = "idx_appel_consultation_date"),
        Index(value = ["cle_appel_consultation"], name = "idx_appel_consultation_key", unique = true)
    ]
)
data class AppelConsultationEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    val nom_appel_consultation: String? = null,
    val date_depot: String? = null,
    val cle_appel_consultation: String? = null,
    val lastUpdated: Long = System.currentTimeMillis()
) {
    companion object {
        const val DATE_FORMAT = "dd/MM/yyyy"
    }
    
    fun isExpired(expirationTimeInMillis: Long): Boolean {
        return (System.currentTimeMillis() - lastUpdated) > expirationTimeInMillis
    }
}
