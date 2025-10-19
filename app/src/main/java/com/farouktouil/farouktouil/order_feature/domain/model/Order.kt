package com.farouktouil.farouktouil.order_feature.domain.model

data class Order(
    val orderId: String,  // Change from UUID to Int
    val date: String,
    val delivererName: String,
    val delivererTime: String,
    val products: List<BoughtProduct>
)