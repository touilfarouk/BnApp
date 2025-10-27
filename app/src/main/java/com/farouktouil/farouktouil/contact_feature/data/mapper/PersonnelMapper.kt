package com.farouktouil.farouktouil.contact_feature.data.mapper

import com.farouktouil.farouktouil.contact_feature.domain.model.Personnel

object PersonnelMapper {

    fun toDomain(apiPersonnel: Personnel): Personnel {
        return apiPersonnel
    }

    fun fromDomain(domainPersonnel: Personnel): Personnel {
        return domainPersonnel
    }

    fun toDomainList(apiPersonnelList: List<Personnel>): List<Personnel> {
        return apiPersonnelList.map { toDomain(it) }
    }
}
