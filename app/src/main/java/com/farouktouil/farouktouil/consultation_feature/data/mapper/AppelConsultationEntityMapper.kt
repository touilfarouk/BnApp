package com.farouktouil.farouktouil.consultation_feature.data.mapper

import com.farouktouil.farouktouil.consultation_feature.data.local.entity.AppelConsultationEntity
import com.farouktouil.farouktouil.consultation_feature.domain.model.AppelConsultation

fun AppelConsultationEntity.toDomain(): AppelConsultation {
    return AppelConsultation(
        id = cle_appel_consultation,
        nom_appel_consultation = nom_appel_consultation,
        date_depot = date_depot,
        cle_appel_consultation = cle_appel_consultation.toString(),
        jour_depot = jour_depot,
        date_fin_evaluation = date_fin_evaluation,
        attribution = attribution,
        num_tender = num_tender,
        download_count = download_count,
        code = code
    )
}
