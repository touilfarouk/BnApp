package com.farouktouil.farouktouil.contact_feature.domain.model

data class Personnel(
    val id: String,
    val username: String?,
    val nom: String?,
    val prenom: String?,
    val fonction: String?,
    val structure: String?,
    val active: String
) {
    val fullName: String
        get() = "${prenom ?: ""} ${nom ?: ""}".trim()

    val displayStructure: String
        get() = structure?.trim() ?: ""

    val displayFunction: String
        get() = fonction?.trim() ?: ""

    val email: String
        get() = if (username.isNullOrBlank()) "" else "$username@bneder.dz"

    val isActive: Boolean
        get() = active == "1"
}
