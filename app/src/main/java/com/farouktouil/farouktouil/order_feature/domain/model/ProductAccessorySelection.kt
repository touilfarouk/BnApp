package com.farouktouil.farouktouil.order_feature.domain.model

import com.farouktouil.farouktouil.core.domain.model.AccessoryType

data class ProductAccessorySelection(
    val productId: Int,
    val selectedTypes: Set<AccessoryType>
)
