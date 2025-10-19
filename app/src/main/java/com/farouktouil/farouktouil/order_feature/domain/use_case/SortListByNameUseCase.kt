package com.farouktouil.farouktouil.order_feature.domain.use_case

import com.farouktouil.farouktouil.core.domain.SelectAndSortableByName

class SortListByNameUseCase {

    operator fun <T : SelectAndSortableByName> invoke(list: List<T>): List<T> {
        return list.sortedBy { item ->
            item.name
        }
    }
}
