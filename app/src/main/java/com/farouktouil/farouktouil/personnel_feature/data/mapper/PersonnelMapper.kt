package com.farouktouil.farouktouil.personnel_feature.data.mapper

import com.farouktouil.farouktouil.personnel_feature.data.local.PersonnelEntity
import com.farouktouil.farouktouil.personnel_feature.domain.model.Personnel

fun Personnel.toEntity(): PersonnelEntity {
    return PersonnelEntity(
        id = this.id ?: "",
        nom = this.nom,
        prenom = this.prenom,
        structure = this.structure,
        username = this.username,
        fonction = this.fonction,
        active = this.active?.toIntOrNull()
    )
}

fun List<Personnel>.toEntities(): List<PersonnelEntity> {
    return this.map { it.toEntity() }
}
