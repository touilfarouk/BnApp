package com.farouktouil.farouktouil.order_feature.presentation.state

data class OrderDetailListItem(
    val orderId: String,
    val structureName: String,
    val deliveryTime: String,
    val orderDate: String,
    val products: List<ProductListItem>
)
