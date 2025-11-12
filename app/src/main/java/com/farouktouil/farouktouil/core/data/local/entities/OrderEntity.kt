package com.farouktouil.farouktouil.core.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class OrderEntity(
    @PrimaryKey val orderId: String,
    val date:String,
    @ColumnInfo(name = "delivererTime")
    val deliveryTime:String,
    @ColumnInfo(name = "delivererName")
    val structureName:String
)
