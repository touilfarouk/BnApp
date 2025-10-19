package com.farouktouil.farouktouil.core.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey



@Entity()
data class DelivererEntity(
    @PrimaryKey(autoGenerate = true)
    val delivererId: Int = 0,  // Auto-incremented primary key
    val name: String
)

