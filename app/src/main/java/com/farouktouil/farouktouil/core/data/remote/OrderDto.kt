package com.farouktouil.farouktouil.core.data.remote

data class OrderDto(
    val orderId: String,
    val date: String,
    val delivererTime: String,
    val delivererName: String,
    val products: List<ProductDto> = emptyList(),
    val orderProductDetails: List<OrderProductDto> = emptyList()
)