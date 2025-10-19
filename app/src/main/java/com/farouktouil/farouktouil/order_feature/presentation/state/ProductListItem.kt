package com.farouktouil.farouktouil.order_feature.presentation.state

data class ProductListItem(
    val id: Int, // Non-nullable
    val name:String,
    val label: String,
    val pricePerAmount: Float,
    val selectedAmount:Int,
    val isExpanded:Boolean
)
