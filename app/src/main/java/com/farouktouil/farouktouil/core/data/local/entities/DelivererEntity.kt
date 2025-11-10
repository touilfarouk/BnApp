package com.farouktouil.farouktouil.core.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "structures")
data class DelivererEntity(
    @PrimaryKey(autoGenerate = true)
    val delivererId: Int = 0,
    val name: String
)

