package com.farouktouil.farouktouil.core.domain.model


import com.farouktouil.farouktouil.core.domain.SelectAndSortableByName

data class Product(
    val productId: Int=0, // Nullable for new entries
    override val name: String,
    val label: String = "", // Product label/description
    val pricePerAmount: Float,
    val quantity: Int = 0,
    val minQuantity: Int = 0,
    val maxQuantity: Int = 100, // Changed from 1000 to 100 as more reasonable default
    val belongsToDeliverer: Int,
    val barcode: String = "" // Barcode/QR code data for the product
): SelectAndSortableByName