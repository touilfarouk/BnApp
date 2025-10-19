package com.farouktouil.farouktouil.deliverer_feature.data.mapper

import com.farouktouil.farouktouil.core.data.local.entities.DelivererEntity
import com.farouktouil.farouktouil.core.domain.model.Deliverer
import com.farouktouil.farouktouil.core.domain.model.Product

fun DelivererEntity.toDeliverer(products:List<Product>): Deliverer {
    return Deliverer(
        delivererId = delivererId,
        name = name,
        products = products
    )
}