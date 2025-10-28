package com.farouktouil.farouktouil.personnel_feature.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "personnel")
data class PersonnelEntity(
    @PrimaryKey
    val id: String,
    val nom: String?,
    val prenom: String?,
    val structure: String?,
    val username: String?,
    val fonction: String?,
    val active: Int?
)
