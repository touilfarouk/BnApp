package com.farouktouil.farouktouil.order_feature.domain.repository

import com.farouktouil.farouktouil.core.domain.model.AccessoryType
import com.farouktouil.farouktouil.core.domain.model.Product
import com.farouktouil.farouktouil.core.domain.model.Structure
import com.farouktouil.farouktouil.order_feature.domain.model.Order
import com.farouktouil.farouktouil.order_feature.domain.model.ProductAccessorySelection
import kotlinx.coroutines.flow.Flow

interface OrderRepository {

    suspend fun insertOrder(order: Order)

    suspend fun deleteOrder(orderId: String) // Added delete function

    suspend fun getOrders():List<Order>

    fun getStructures(): Flow<List<Structure>>

    fun getProductsForStructure(structureName: String): Flow<List<Product>>

    fun observeProductAccessories(): Flow<List<ProductAccessorySelection>>

    suspend fun updateProductAccessories(productId: Int, selectedTypes: Set<AccessoryType>)
}