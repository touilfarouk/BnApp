package com.farouktouil.farouktouil.core.data.remote

data class DelivererDto(
    val id: Int,
    val name: String,
    val products: List<ProductDto> = emptyList()
)