package com.farouktouil.farouktouil.product_feature.domain.useCase

import com.farouktouil.farouktouil.core.domain.model.Product
import com.farouktouil.farouktouil.product_feature.domain.repository.ProductsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetProductsForDelivererUseCase @Inject constructor(
    private val repository: ProductsRepository
) {
    operator fun invoke(delivererId: Int): Flow<List<Product>> {
        return repository.getProductsForDeliverer(delivererId)
    }
}