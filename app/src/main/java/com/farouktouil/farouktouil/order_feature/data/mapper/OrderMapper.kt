package com.farouktouil.farouktouil.order_feature.data.mapper

import com.farouktouil.farouktouil.core.data.local.entities.OrderEntity
import com.farouktouil.farouktouil.order_feature.domain.model.Order

fun Order.toOrderEntity(): OrderEntity {
    val resolvedPersonnel = if (personnelNames.isNotEmpty()) {
        personnelNames
    } else {
        products.mapNotNull { it.assignedPersonnelName }.distinct()
    }

    val resolvedSummary = if (productsSummary.isNotEmpty()) {
        productsSummary
    } else {
        products.map { product ->
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
    }

    return OrderEntity(
        orderId = orderId,
        date = date,
        checkoutTime = checkoutTime,
        structureName = structureName,
        personnelName = resolvedPersonnel.takeIf { it.isNotEmpty() }?.joinToString(", "),
        productsSummary = resolvedSummary.joinToString(separator = "\n")
    )
}