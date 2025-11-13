package com.farouktouil.farouktouil.order_feature.domain.model

data class Order(
    val orderId: String,  // Change from UUID to Int
    val date: String,
    val structureName: String,
    val checkoutTime: String,
    val products: List<BoughtProduct>,
    val personnelNames: List<String> = emptyList(),
    val productsSummary: List<String> = emptyList()
)