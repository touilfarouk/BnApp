package com.farouktouil.farouktouil.product_feature.data.mapper

import com.farouktouil.farouktouil.core.data.local.entities.ProductEntity
import com.farouktouil.farouktouil.core.domain.model.Product

fun Product.toProductEntity(): ProductEntity {
    return ProductEntity(
        productId = productId ?: 0, // Use 0 if null
        name = name,
        label = label,
        pricePerAmount = pricePerAmount,
        quantity = quantity,
        minQuantity = minQuantity,
        maxQuantity = maxQuantity,
        belongsToDeliverer = belongsToDeliverer // Already Int in Product model
    )
}

fun ProductEntity.toProduct(): Product {
    return Product(
        productId = productId, // Non-nullable in ProductEntity
        name = name,
        label = label,
        pricePerAmount = pricePerAmount,
        quantity = quantity,
        minQuantity = minQuantity,
        maxQuantity = maxQuantity,
        belongsToDeliverer = belongsToDeliverer // Already Int in ProductEntity
    )
}

