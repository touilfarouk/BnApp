package com.farouktouil.farouktouil.order_feature.data.mapper

import com.farouktouil.farouktouil.core.data.local.entities.OrderWithProductsDataObject
import com.farouktouil.farouktouil.order_feature.domain.model.BoughtProduct
import com.farouktouil.farouktouil.order_feature.domain.model.Order

fun OrderWithProductsDataObject.toOrder(): Order {
    return Order(
        orderId = orderEntity.orderId.toString(),
        date = orderEntity.date,
        delivererName = orderEntity.delivererName,
        delivererTime = orderEntity.delivererTime,
        products = products.zip(orderProductEntities) { product, orderProduct ->
            BoughtProduct(
                productId = product.productId,
                name = product.name,
                label = product.label,
                pricePerAmount = product.pricePerAmount,
                amount = orderProduct.amount
            )
        }
    )
}
