package com.farouktouil.farouktouil.core.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "products",
)
data class ProductEntity(
    @PrimaryKey(autoGenerate = true)
    val productId: Int = 0,
    val name: String,
    val label: String = "", // Product label/description
    val pricePerAmount: Float,
    val quantity: Int = 0,
    val minQuantity: Int = 0,
    val maxQuantity: Int = 100, // Changed from 1000 to 100 as more reasonable default
    val structureName: String?,
    val assignedPersonnelId: Int? = null,
    val assignedPersonnelName: String? = null,
    val barcode: String = "" // Barcode/QR code data for the product
)
