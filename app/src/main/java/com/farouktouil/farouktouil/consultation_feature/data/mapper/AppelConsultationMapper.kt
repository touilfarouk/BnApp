package com.farouktouil.farouktouil.consultation_feature.data.mapper

import com.farouktouil.farouktouil.consultation_feature.data.local.entity.AppelConsultationEntity
import com.farouktouil.farouktouil.consultation_feature.data.remote.dto.AppelConsultationDto
import com.farouktouil.farouktouil.consultation_feature.domain.model.AppelConsultation

fun AppelConsultationDto.toDomain(): AppelConsultation {
    return AppelConsultation(
        id = cleAppelConsultation,
        nom_appel_consultation = nomAppelConsultation ?: "",
        date_depot = dateDepot,
        cle_appel_consultation = cleAppelConsultation.toString(),
        jour_depot = jourDepot,
        date_fin_evaluation = dateFinEvaluation,
        attribution = attribution,
        num_tender = numTender,
        download_count = downloadCount,
        code = code
    )
}

fun List<AppelConsultationDto>.toDomainList(): List<AppelConsultation> {
    return this.map { it.toDomain() }
}

fun AppelConsultation.toEntity(): AppelConsultationEntity {
    return AppelConsultationEntity(
        cle_appel_consultation = id,
        nom_appel_consultation = nom_appel_consultation,
        date_depot = date_depot,
        jour_depot = jour_depot,
        date_fin_evaluation = date_fin_evaluation,
        attribution = attribution,
        num_tender = num_tender,
        download_count = download_count,
        code = code
    )
}

fun List<AppelConsultation>.toEntities(): List<AppelConsultationEntity> {
    return this.map { it.toEntity() }
}
