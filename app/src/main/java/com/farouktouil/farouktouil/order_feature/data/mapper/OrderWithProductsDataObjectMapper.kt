package com.farouktouil.farouktouil.order_feature.data.mapper

import com.farouktouil.farouktouil.core.data.local.entities.OrderWithProductsDataObject
import com.farouktouil.farouktouil.order_feature.domain.model.BoughtProduct
import com.farouktouil.farouktouil.order_feature.domain.model.Order

fun OrderWithProductsDataObject.toOrder(): Order {
    val domainProducts = products.zip(orderProductEntities) { product, orderProduct ->
        BoughtProduct(
            productId = product.productId,
            name = product.name,
            label = product.label,
            pricePerAmount = product.pricePerAmount,
            amount = orderProduct.amount,
            structureName = product.structureName,
            assignedPersonnelName = product.assignedPersonnelName
        )
    }

    val personnelFromEntity = orderEntity.personnelName
        ?.split(",")
        ?.map { it.trim() }
        ?.filter { it.isNotEmpty() }
        ?: emptyList()

    val summaryFromEntity = orderEntity.productsSummary
        .lines()
        .map { it.trim() }
        .filter { it.isNotEmpty() }

    val computedPersonnel = domainProducts.mapNotNull { it.assignedPersonnelName }.distinct()

    val computedSummary = domainProducts.map { product ->
        buildString {
            append("${product.amount} x ${product.name}")
            if (product.label.isNotBlank()) {
                append(" — ${product.label}")
            }
            product.assignedPersonnelName?.takeIf { it.isNotBlank() }?.let {
                append(" | Personnel : $it")
            }
            product.structureName?.takeIf { it.isNotBlank() }?.let {
                append(" | Structure : $it")
            }
        }
    }

    return Order(
        orderId = orderEntity.orderId.toString(),
        date = orderEntity.date,
        structureName = orderEntity.structureName,
        checkoutTime = orderEntity.checkoutTime,
        products = domainProducts,
        personnelNames = if (personnelFromEntity.isNotEmpty()) personnelFromEntity else computedPersonnel,
        productsSummary = if (summaryFromEntity.isNotEmpty()) summaryFromEntity else computedSummary
    )
}
