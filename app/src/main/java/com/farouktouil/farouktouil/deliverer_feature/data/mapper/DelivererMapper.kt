package com.farouktouil.farouktouil.deliverer_feature.data.mapper

import com.farouktouil.farouktouil.core.data.local.entities.DelivererEntity
import com.farouktouil.farouktouil.core.domain.model.Deliverer
import com.farouktouil.farouktouil.deliverer_feature.data.remote.DelivererDto

fun DelivererDto.toDeliverer(): Deliverer {
    return Deliverer(
        delivererId = id.toIntOrNull() ?: 0,
        name = name
    )
}

fun Deliverer.toDelivererEntity(): DelivererEntity {
    return DelivererEntity(
        delivererId = delivererId,
        name = name
    )
}

fun DelivererEntity.toDeliverer(): Deliverer {
    return Deliverer(
        delivererId = delivererId,
        name = name
    )
}
