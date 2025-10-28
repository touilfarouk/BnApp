package com.farouktouil.farouktouil.consultation_feature.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "appel_consultation")
data class AppelConsultationEntity(
    @PrimaryKey
    val id: Int,
    val nom_appel_consultation: String?,
    val date_depot: String?,
    val cle_appel_consultation: String?
)
