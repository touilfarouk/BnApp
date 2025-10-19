package com.farouktouil.farouktouil.deliverer_feature.data.remote

import com.google.gson.annotations.SerializedName

data class DelivererDto(
    val id: String,
    val name: String,
    @SerializedName("created_at")
    val createdAt: String
)