package com.farouktouil.farouktouil.order_feature.presentation.mapper

import com.farouktouil.farouktouil.core.domain.model.Structure
import com.farouktouil.farouktouil.order_feature.presentation.state.StructureListItem

fun Structure.toStructureListItem(): StructureListItem {
    return StructureListItem(
        name = name
    )
}