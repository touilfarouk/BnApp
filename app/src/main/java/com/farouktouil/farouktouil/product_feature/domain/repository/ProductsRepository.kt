package com.farouktouil.farouktouil.product_feature.domain.repository

import com.farouktouil.farouktouil.core.data.local.entities.ProductEntity
import com.farouktouil.farouktouil.core.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface ProductsRepository {
    suspend fun insert(product: Product)
    suspend fun update(product: Product)
    suspend fun delete(product: Product)
    fun getAllProducts(): Flow<List<Product>>
    fun getProductsForDeliverer(delivererId: Int): Flow<List<Product>> // ✅ Ensure correct return type
}