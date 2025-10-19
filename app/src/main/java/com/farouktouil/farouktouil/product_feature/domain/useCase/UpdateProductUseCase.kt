package com.farouktouil.farouktouil.product_feature.domain.useCase

import com.farouktouil.farouktouil.core.domain.model.Product
import com.farouktouil.farouktouil.product_feature.domain.repository.ProductsRepository
import javax.inject.Inject


class UpdateProductUseCase @Inject constructor(private val productRepository: ProductsRepository) {

    suspend operator fun invoke(product: Product) = productRepository.update(product)

}
