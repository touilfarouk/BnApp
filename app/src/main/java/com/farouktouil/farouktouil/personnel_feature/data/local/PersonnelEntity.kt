package com.farouktouil.farouktouil.personnel_feature.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "personnel")
data class PersonnelEntity(
    @PrimaryKey
    val id: Int,
    val username: String?,
    val password: String?,
    val name: String?,
    val nom: String?,
    val prenom: String?,
    val fonction: String?,
    val structure: String?,
    val maildir: String?,
    val quota: String?,
    val localPart: String?,
    val domain: String?,
    val matricule: Int?,
    val active: Int?,
    val mdpChanged: Int?,
    val dateRegister: String?,
    val modified: String?
)
