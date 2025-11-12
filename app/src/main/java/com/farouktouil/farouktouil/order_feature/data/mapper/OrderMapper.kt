package com.farouktouil.farouktouil.order_feature.data.mapper

import com.farouktouil.farouktouil.core.data.local.entities.OrderEntity
import com.farouktouil.farouktouil.order_feature.domain.model.Order

fun Order.toOrderEntity(): OrderEntity {
    return OrderEntity(
        orderId = orderId,
        date = date,
        deliveryTime = deliveryTime,
        structureName = structureName
    )
}