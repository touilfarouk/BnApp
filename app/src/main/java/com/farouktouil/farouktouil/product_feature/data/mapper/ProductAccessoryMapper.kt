package com.farouktouil.farouktouil.product_feature.data.mapper

import com.farouktouil.farouktouil.core.data.local.entities.ProductAccessoryEntity
import com.farouktouil.farouktouil.core.domain.model.AccessoryType

fun Set<AccessoryType>.toAccessoryEntity(productId: Int): ProductAccessoryEntity {
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

fun ProductAccessoryEntity.toAccessorySet(): Set<AccessoryType> {
    val selected = mutableSetOf<AccessoryType>()
    if (hasMouse) selected.add(AccessoryType.MOUSE)
    if (hasKeyboard) selected.add(AccessoryType.KEYBOARD)
    if (hasUps) selected.add(AccessoryType.UPS)
    if (hasChair) selected.add(AccessoryType.CHAIR)
    if (hasDesk) selected.add(AccessoryType.DESK)
    if (hasPrinter) selected.add(AccessoryType.PRINTER)
    return selected
}
