package com.farouktouil.farouktouil.order_feature.domain.repository

import com.farouktouil.farouktouil.core.domain.model.Product
import com.farouktouil.farouktouil.core.domain.model.Structure
import com.farouktouil.farouktouil.order_feature.domain.model.Order
import kotlinx.coroutines.flow.Flow

interface OrderRepository {

    suspend fun insertOrder(order: Order)

    suspend fun deleteOrder(orderId: String) // Added delete function

    suspend fun getOrders():List<Order>

    fun getStructures(): Flow<List<Structure>>

    fun getProductsForStructure(structureName: String): Flow<List<Product>>
}