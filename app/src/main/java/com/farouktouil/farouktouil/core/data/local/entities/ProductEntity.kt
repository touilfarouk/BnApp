package com.farouktouil.farouktouil.core.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.farouktouil.farouktouil.personnel_feature.data.local.PersonnelEntity

@Entity(
    tableName = "products",
    foreignKeys = [ForeignKey(
        entity = DelivererEntity::class,
        parentColumns = ["delivererId"],
        childColumns = ["belongsToDeliverer"],
        onDelete = ForeignKey.CASCADE
    ),
        ForeignKey(
            entity = PersonnelEntity::class,
            parentColumns = ["id"],
            childColumns = ["assignedPersonnelId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index(value = ["belongsToDeliverer"]),
        Index(value = ["assignedPersonnelId"])
    ]
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
    val assignedPersonnelId: Int? = null,
    val barcode: String = "" // Barcode/QR code data for the product
)
