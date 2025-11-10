package com.farouktouil.farouktouil.personnel_feature.data.mapper

import com.farouktouil.farouktouil.personnel_feature.data.local.PersonnelEntity
import com.farouktouil.farouktouil.personnel_feature.data.remote.PersonnelDto
import com.farouktouil.farouktouil.personnel_feature.domain.model.Personnel

fun PersonnelEntity.toDomain(): Personnel = Personnel(
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

fun List<PersonnelDto>.toEntities(): List<PersonnelEntity> = map { it.toEntity() }

fun PersonnelDto.toEntity(): PersonnelEntity = PersonnelEntity(
    id = id,
    username = username?.trim()?.ifEmpty { null },
    password = password,
    name = name?.trim()?.ifEmpty { null },
    nom = nom?.trim()?.ifEmpty { null },
    prenom = prenom?.trim()?.ifEmpty { null },
    fonction = fonction?.trim()?.ifEmpty { null },
    structure = structure?.trim()?.ifEmpty { null },
    maildir = maildir?.trim()?.ifEmpty { null },
    quota = quota?.trim()?.ifEmpty { null },
    localPart = localPart?.trim()?.ifEmpty { null },
    domain = domain?.trim()?.ifEmpty { null },
    matricule = matricule,
    active = active,
    mdpChanged = mdpChanged,
    dateRegister = dateRegister,
    modified = modified
)

fun PersonnelDto.toDomain(): Personnel = Personnel(
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
