package com.farouktouil.farouktouil.product_feature.domain.useCase

import com.farouktouil.farouktouil.core.domain.model.AccessoryType
import com.farouktouil.farouktouil.product_feature.domain.repository.ProductsRepository
import javax.inject.Inject

class GetProductAccessoriesUseCase @Inject constructor(
    private val productsRepository: ProductsRepository
) {
    suspend operator fun invoke(productId: Int): Set<AccessoryType> {
        return productsRepository.getAccessories(productId)
    }
}
