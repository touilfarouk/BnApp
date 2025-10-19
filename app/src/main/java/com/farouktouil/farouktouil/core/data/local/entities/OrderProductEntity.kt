package com.farouktouil.farouktouil.core.data.local.entities

import androidx.room.Entity

@Entity(primaryKeys = ["orderId","productId"])
data class OrderProductEntity(
    val orderId: String,
    val productId:Int,
    val amount:Int
)