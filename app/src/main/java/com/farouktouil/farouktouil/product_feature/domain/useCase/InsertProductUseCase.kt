package com.farouktouil.farouktouil.product_feature.domain.useCase

import com.farouktouil.farouktouil.core.domain.model.Product
import com.farouktouil.farouktouil.core.domain.model.AccessoryType
import com.farouktouil.farouktouil.product_feature.domain.repository.ProductsRepository
import javax.inject.Inject

class InsertProductUseCase @Inject constructor(private val productRepository: ProductsRepository) {
    suspend operator fun invoke(
        product: Product,
        accessories: Set<AccessoryType>
    ): Int {
        val productId = productRepository.insert(product)
        productRepository.upsertAccessories(productId, accessories)

        // Fire-and-forget remote sync; failures are logged inside repository implementation.
        productRepository.pushProductToRemote(
            product.copy(productId = productId),
            accessories
        )

        return productId
    }
}
