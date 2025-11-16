package com.farouktouil.farouktouil.product_feature.data.repository

import com.farouktouil.farouktouil.core.data.local.ProductAccessoryDao
import com.farouktouil.farouktouil.core.data.local.ProductDao
import com.farouktouil.farouktouil.core.domain.model.AccessoryType
import com.farouktouil.farouktouil.core.domain.model.Product
import com.farouktouil.farouktouil.product_feature.data.mapper.toAccessoryEntity
import com.farouktouil.farouktouil.product_feature.data.mapper.toAccessorySet
import com.farouktouil.farouktouil.product_feature.data.mapper.toProduct
import com.farouktouil.farouktouil.product_feature.data.mapper.toProductEntity
import com.farouktouil.farouktouil.product_feature.domain.repository.ProductsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val productDao: ProductDao,
    private val productAccessoryDao: ProductAccessoryDao
) : ProductsRepository {

    override suspend fun insert(product: Product): Int {
        return productDao.insertProduct(product.toProductEntity()).toInt()
    }

    override suspend fun update(product: Product) {
        productDao.updateProduct(product.toProductEntity())
    }

    override suspend fun delete(product: Product) {
        productDao.deleteProduct(product.toProductEntity())
        if (product.productId != 0) {
            productAccessoryDao.deleteForProduct(product.productId)
        }
    }

    override fun getAllProducts(): Flow<List<Product>> {
        return productDao.getAllProducts()
            .map { list -> list.map { it.toProduct() } }
    }

    override fun getProductsForStructure(structureName: String): Flow<List<Product>> {
        return productDao.getProductsForStructure(structureName)
            .map { list -> list.map { it.toProduct() } }
    }

    override suspend fun upsertAccessories(productId: Int, accessories: Set<AccessoryType>) {
        if (productId <= 0) return
        if (accessories.isEmpty()) {
            productAccessoryDao.deleteForProduct(productId)
        } else {
            productAccessoryDao.upsert(accessories.toAccessoryEntity(productId))
        }
    }

    override suspend fun getAccessories(productId: Int): Set<AccessoryType> {
        if (productId <= 0) return emptySet()
        return productAccessoryDao.getAccessoriesForProduct(productId)?.toAccessorySet() ?: emptySet()
    }
}
