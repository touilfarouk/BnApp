package com.farouktouil.farouktouil.order_feature.data.mapper

import com.farouktouil.farouktouil.core.data.local.entities.OrderEntity
import com.farouktouil.farouktouil.order_feature.domain.model.Order

fun Order.toOrderEntity(delivererName:String):OrderEntity{
    return OrderEntity(
        orderId = orderId,
        date = date,
        delivererTime = delivererTime,
        delivererName = delivererName
    )
}