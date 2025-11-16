package com.farouktouil.farouktouil.product_feature.domain.repository

import com.farouktouil.farouktouil.core.domain.model.Product
import com.farouktouil.farouktouil.core.domain.model.AccessoryType
import kotlinx.coroutines.flow.Flow

interface ProductsRepository {
    suspend fun insert(product: Product): Int
    suspend fun update(product: Product)
    suspend fun delete(product: Product)
    fun getAllProducts(): Flow<List<Product>>
    fun getProductsForStructure(structureName: String): Flow<List<Product>>
    suspend fun upsertAccessories(productId: Int, accessories: Set<AccessoryType>)
    suspend fun getAccessories(productId: Int): Set<AccessoryType>
    suspend fun pushProductToRemote(product: Product, accessories: Set<AccessoryType>)
    suspend fun getProductById(productId: Int): Product?
}