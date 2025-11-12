package com.farouktouil.farouktouil.order_feature.presentation.state

data class OrderListItem(
    val orderId: String,
    val structureName: String,
    val totalAmount: Double,
    val orderDate: String
)