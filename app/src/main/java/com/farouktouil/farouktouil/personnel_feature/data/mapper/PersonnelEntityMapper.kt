package com.farouktouil.farouktouil.personnel_feature.data.mapper

import com.farouktouil.farouktouil.personnel_feature.data.local.PersonnelEntity
import com.farouktouil.farouktouil.personnel_feature.domain.model.Personnel

fun PersonnelEntity.toDomain(): Personnel {
    return Personnel(
        id = this.id,
        nom = this.nom,
        prenom = this.prenom,
        username = this.username,
        fonction = this.fonction,
        structure = this.structure,
        active = this.active?.toString()
    )
}
