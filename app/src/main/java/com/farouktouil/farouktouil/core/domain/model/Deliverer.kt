package com.farouktouil.farouktouil.core.domain.model

import com.farouktouil.farouktouil.core.domain.SelectAndSortableByName

data class Deliverer(
    val delivererId: Int=0, // Nullable for new entries
    override val name:String,
    val products:List<Product> = emptyList() // Default to empty list
):SelectAndSortableByName
