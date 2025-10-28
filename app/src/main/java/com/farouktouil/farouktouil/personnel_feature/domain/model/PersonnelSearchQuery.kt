package com.farouktouil.farouktouil.personnel_feature.domain.model

data class PersonnelSearchQuery(
    val name: String? = null,
    val structure: String? = null,
    val active: Int? = null
)
