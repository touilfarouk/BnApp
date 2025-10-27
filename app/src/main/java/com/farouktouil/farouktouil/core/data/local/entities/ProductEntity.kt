package com.farouktouil.farouktouil.core.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "products",
    foreignKeys = [ForeignKey(
        entity = DelivererEntity::class,
        parentColumns = ["delivererId"],
        childColumns = ["belongsToDeliverer"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["belongsToDeliverer"])]
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
    val belongsToDeliverer: Int,
    val barcode: String = "" // Barcode/QR code data for the product
)
