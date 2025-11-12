package com.farouktouil.farouktouil.core.domain.model

import com.farouktouil.farouktouil.core.domain.SelectAndSortableByName

data class Structure(
    override val name: String
) : SelectAndSortableByName
