package com.farouktouil.farouktouil.deliverer_feature.data.remote

data class DelivererResponse(
    val deliverers: List<DelivererDto>,
    val page: Int,
    val pageSize: Int,
    val total: Int,
    val totalPages: Int
)