package com.farouktouil.farouktouil.order_feature.domain.model

data class BoughtProduct(
    val productId:Int,
    val name:String,
    val label: String,
    val pricePerAmount:Float,
    val amount:Int
)
