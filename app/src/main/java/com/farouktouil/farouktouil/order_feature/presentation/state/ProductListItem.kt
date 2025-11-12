package com.farouktouil.farouktouil.order_feature.presentation.state

import com.farouktouil.farouktouil.core.domain.model.AccessoryType

data class ProductListItem(
    val id: Int, // Non-nullable
    val name:String,
    val label: String,
    val structureName: String? = null,
    val assignedPersonnelName: String? = null,
    val pricePerAmount: Float,
    val selectedAmount:Int,
    val isExpanded:Boolean,
    val accessories: Set<AccessoryType> = emptySet()
)
