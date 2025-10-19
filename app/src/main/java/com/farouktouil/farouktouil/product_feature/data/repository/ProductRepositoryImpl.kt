package com.farouktouil.farouktouil.product_feature.data.repository

import com.farouktouil.farouktouil.core.data.local.ProductDao
import com.farouktouil.farouktouil.core.data.local.entities.ProductEntity

import com.farouktouil.farouktouil.core.domain.model.Product
import com.farouktouil.farouktouil.product_feature.data.mapper.toProductEntity
import com.farouktouil.farouktouil.product_feature.data.mapper.toProduct
import com.farouktouil.farouktouil.product_feature.domain.repository.ProductsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val productDao: ProductDao
) : ProductsRepository {

    override suspend fun insert(product: Product) {
        productDao.insertProduct(product.toProductEntity())
    }

    override suspend fun update(product: Product) {
        productDao.updateProduct(product.toProductEntity())
    }

    override suspend fun delete(product: Product) {
        productDao.deleteProduct(product.toProductEntity())
    }

    override fun getAllProducts(): Flow<List<Product>> {
        return productDao.getAllProducts()
            .map { list -> list.map { it.toProduct() } } // ✅ Apply your existing mapper
    }

    override fun getProductsForDeliverer(delivererId: Int): Flow<List<Product>> {
        return productDao.getProductsForDeliverer(delivererId)
            .map { list -> list.map { it.toProduct() } } // ✅ Apply the mapper correctly
    }
}
