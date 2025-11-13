

package com.farouktouil.farouktouil.core.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class OrderEntity(
    @PrimaryKey val orderId: String,
    val date: String,
    val checkoutTime: String,
    val structureName: String,
    val personnelName: String? = null,
    val productsSummary: String = ""
)
