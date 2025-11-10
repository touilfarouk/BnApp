package com.farouktouil.farouktouil.personnel_feature.domain.model

data class Personnel(
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
) {
    val fullName: String
        get() = listOfNotNull(prenom?.trim(), nom?.trim())
            .filter { it.isNotEmpty() }
            .joinToString(" ")

    val displayStructure: String?
        get() = structure?.trim().takeUnless { it.isNullOrEmpty() }

    val displayFunction: String?
        get() = fonction?.trim().takeUnless { it.isNullOrEmpty() }

    val email: String?
        get() = username?.trim().takeUnless { it.isNullOrEmpty() }?.let { "$it@bneder.dz" }

    val isActive: Boolean
        get() = active == 1
}
