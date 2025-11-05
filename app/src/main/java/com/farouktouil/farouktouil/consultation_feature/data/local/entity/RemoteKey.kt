package com.farouktouil.farouktouil.consultation_feature.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "consultation_remote_keys",
    indices = [
        Index("consultationId"),
        Index("query")
    ]
)
data class RemoteKey(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val consultationId: Int,
    val prevKey: Int?,
    val nextKey: Int?,
    val query: String? = null
)