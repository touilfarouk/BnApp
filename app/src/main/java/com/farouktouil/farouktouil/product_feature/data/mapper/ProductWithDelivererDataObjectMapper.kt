package com.farouktouil.farouktouil.product_feature.data.mapper

import com.farouktouil.farouktouil.core.data.local.entities.ProductEntity
import com.farouktouil.farouktouil.core.domain.model.Product


fun Product.toProductEntity(delivererId:Int): ProductEntity {
    return ProductEntity(
        productId = productId,
        name = name,
        pricePerAmount = pricePerAmount,
        belongsToDeliverer = delivererId
    )
}