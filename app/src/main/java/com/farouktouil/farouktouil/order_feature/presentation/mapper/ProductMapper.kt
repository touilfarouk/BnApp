package com.farouktouil.farouktouil.order_feature.presentation.mapper

import com.farouktouil.farouktouil.core.domain.model.Product
import com.farouktouil.farouktouil.order_feature.domain.model.BoughtProduct
import com.farouktouil.farouktouil.order_feature.presentation.state.ProductListItem

fun BoughtProduct.toProductListItem(): ProductListItem {
    return ProductListItem(
        id = productId,
        name = name,
        label = label,
        pricePerAmount = pricePerAmount,
        selectedAmount = amount,
        isExpanded = false
    )
}

fun Product.toProductListItem(): ProductListItem {
    return ProductListItem(
        id = productId ?: 0, // Use 0 if null
        name = name,
        label = label,
        pricePerAmount = pricePerAmount,
        selectedAmount = 0,
        isExpanded = false
    )
}

fun ProductListItem.toBoughtProduct(): BoughtProduct {
    return BoughtProduct(
        productId = id, // Non-nullable in ProductListItem
        name = name,
        label = label,
        pricePerAmount = pricePerAmount,
        amount = selectedAmount
    )
}