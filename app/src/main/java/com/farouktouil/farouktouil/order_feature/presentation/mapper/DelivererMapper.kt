package com.farouktouil.farouktouil.order_feature.presentation.mapper

import com.farouktouil.farouktouil.core.domain.model.Deliverer
import com.farouktouil.farouktouil.order_feature.presentation.state.DelivererListItem

fun Deliverer.toDelivererListItem():DelivererListItem{
    return DelivererListItem(
        delivererId = delivererId,
        name = name
    )
}