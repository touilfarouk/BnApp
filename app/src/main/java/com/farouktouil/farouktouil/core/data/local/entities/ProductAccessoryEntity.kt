package com.farouktouil.farouktouil.core.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "product_accessories")
data class ProductAccessoryEntity(
    @PrimaryKey
    val productId: Int,
    val hasMouse: Boolean = false,
    val hasKeyboard: Boolean = false,
    val hasUps: Boolean = false,
    val hasChair: Boolean = false,
    val hasDesk: Boolean = false,
    val hasPrinter: Boolean = false
)
