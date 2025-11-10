package com.farouktouil.farouktouil.personnel_feature.data.mapper

import com.farouktouil.farouktouil.personnel_feature.data.local.PersonnelEntity
import com.farouktouil.farouktouil.personnel_feature.domain.model.Personnel

fun Personnel.toEntity(): PersonnelEntity = PersonnelEntity(
    id = id,
    username = username,
    password = password,
    name = name,
    nom = nom,
    prenom = prenom,
    fonction = fonction,
    structure = structure,
    maildir = maildir,
    quota = quota,
    localPart = localPart,
    domain = domain,
    matricule = matricule,
    active = active,
    mdpChanged = mdpChanged,
    dateRegister = dateRegister,
    modified = modified
)

fun List<Personnel>.toEntities(): List<PersonnelEntity> = map { it.toEntity() }
