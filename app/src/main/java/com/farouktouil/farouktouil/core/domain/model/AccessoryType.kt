package com.farouktouil.farouktouil.core.domain.model

import androidx.annotation.StringRes
import com.farouktouil.farouktouil.R

enum class AccessoryType(@StringRes val labelRes: Int) {
    MOUSE(R.string.accessory_mouse),
    KEYBOARD(R.string.accessory_keyboard),
    UPS(R.string.accessory_ups),
    CHAIR(R.string.accessory_chair),
    DESK(R.string.accessory_desk),
    PRINTER(R.string.accessory_printer)
}
