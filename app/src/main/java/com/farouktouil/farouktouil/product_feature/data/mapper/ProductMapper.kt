package com.farouktouil.farouktouil.product_feature.data.mapper

import com.farouktouil.farouktouil.core.data.local.entities.ProductEntity
import com.farouktouil.farouktouil.core.domain.model.Product

fun Product.toProductEntity(): ProductEntity {
    return ProductEntity(
        productId = productId,
        name = name,
        label = label,
        pricePerAmount = pricePerAmount,
        quantity = quantity,
        minQuantity = minQuantity,
        maxQuantity = maxQuantity,
        structureName = structureName,
        assignedPersonnelId = assignedPersonnelId,
        assignedPersonnelName = assignedPersonnelName
    )
}

fun ProductEntity.toProduct(): Product {
    return Product(
        productId = productId,
        name = name,
        label = label,
        pricePerAmount = pricePerAmount,
        quantity = quantity,
        minQuantity = minQuantity,
        maxQuantity = maxQuantity,
        structureName = structureName,
        assignedPersonnelId = assignedPersonnelId,
        assignedPersonnelName = assignedPersonnelName
    )
}

