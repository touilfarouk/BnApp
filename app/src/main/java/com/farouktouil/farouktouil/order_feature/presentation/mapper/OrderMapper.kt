package com.farouktouil.farouktouil.order_feature.presentation.mapper

import com.farouktouil.farouktouil.order_feature.domain.model.Order
import com.farouktouil.farouktouil.order_feature.presentation.state.OrderDetailListItem
import com.farouktouil.farouktouil.order_feature.presentation.state.OrderListItem

fun Order.toOrderDetailListItem(): OrderDetailListItem {
    return OrderDetailListItem(
        orderId = orderId,
        structureName = structureName,
        checkoutTime = checkoutTime,
        orderDate = date,
        products = products.map { boughtProduct ->
            boughtProduct.toProductListItem()
        }
    )
}

fun Order.toOrderListItem():OrderListItem{
    return OrderListItem(
        orderId = orderId,
        structureName = structureName,
        orderDate = date,
        products = products.map { "${it.amount}x ${it.name}" }
    )
}