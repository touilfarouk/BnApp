package com.farouktouil.farouktouil.core.data.remote

data class ProductDto(
    val id: Int,
    val name: String,
    val pricePerAmount: Float,
    val belongsToDeliverer: Int
)