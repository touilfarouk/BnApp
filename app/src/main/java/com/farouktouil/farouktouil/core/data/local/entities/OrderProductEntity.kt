package com.farouktouil.farouktouil.core.data.local.entities

import androidx.room.Entity
import androidx.room.Index

@Entity(primaryKeys = ["orderId", "productId"], indices = [Index(value = ["productId"])])
data class OrderProductEntity(
    val orderId: String,
    val productId: Int,
    val amount: Int
)
