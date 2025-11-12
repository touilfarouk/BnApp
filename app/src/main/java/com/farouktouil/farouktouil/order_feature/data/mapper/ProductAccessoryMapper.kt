package com.farouktouil.farouktouil.order_feature.data.mapper

import com.farouktouil.farouktouil.core.data.local.entities.ProductAccessoryEntity
import com.farouktouil.farouktouil.core.domain.model.AccessoryType
import com.farouktouil.farouktouil.order_feature.domain.model.ProductAccessorySelection

fun ProductAccessoryEntity.toSelection(): ProductAccessorySelection {
    val selected = buildSet {
        if (hasMouse) add(AccessoryType.MOUSE)
        if (hasKeyboard) add(AccessoryType.KEYBOARD)
        if (hasUps) add(AccessoryType.UPS)
        if (hasChair) add(AccessoryType.CHAIR)
        if (hasDesk) add(AccessoryType.DESK)
        if (hasPrinter) add(AccessoryType.PRINTER)
    }
    return ProductAccessorySelection(
        productId = productId,
        selectedTypes = selected
    )
}

fun Set<AccessoryType>.toEntity(productId: Int): ProductAccessoryEntity {
    return ProductAccessoryEntity(
        productId = productId,
        hasMouse = contains(AccessoryType.MOUSE),
        hasKeyboard = contains(AccessoryType.KEYBOARD),
        hasUps = contains(AccessoryType.UPS),
        hasChair = contains(AccessoryType.CHAIR),
        hasDesk = contains(AccessoryType.DESK),
        hasPrinter = contains(AccessoryType.PRINTER)
    )
}
