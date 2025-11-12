package com.farouktouil.farouktouil.product_feature.domain.useCase

import com.farouktouil.farouktouil.core.domain.model.Product
import com.farouktouil.farouktouil.product_feature.domain.repository.ProductsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetProductsForStructureUseCase @Inject constructor(
    private val repository: ProductsRepository
) {
    operator fun invoke(structureName: String): Flow<List<Product>> {
        return repository.getProductsForStructure(structureName)
    }
}