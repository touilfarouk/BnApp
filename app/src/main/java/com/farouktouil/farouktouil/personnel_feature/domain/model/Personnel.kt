package com.farouktouil.farouktouil.personnel_feature.domain.model

data class Personnel(
    val id: String?,
    val username: String?,
    val nom: String?,
    val prenom: String?,
    val fonction: String?,
    val structure: String?,
    val active: String?,
    val email: String? = "$username@bneder.dz"  // Generate email from username
) {
    val fullName: String
        get() = "$prenom $nom".trim()

    val displayStructure: String?
        get() = structure?.trim()

    val displayFunction: String?
        get() = fonction?.trim()

    val isActive: Boolean
        get() = active == "1"
}
