package com.farouktouil.farouktouil.order_feature.domain.repository

import com.farouktouil.farouktouil.core.domain.model.Deliverer
import com.farouktouil.farouktouil.core.domain.model.Product
import com.farouktouil.farouktouil.order_feature.domain.model.Order
import kotlinx.coroutines.flow.Flow

interface OrderRepository {

    suspend fun insertOrder(order: Order)

    suspend fun deleteOrder(orderId: String) // Added delete function

    suspend fun getOrders():List<Order>

    fun getDeliverers(): Flow<List<Deliverer>>

    fun getProductsForDeliverer(delivererId: Int): Flow<List<Product>>

    suspend fun getDelivererNameById(delivererId: Int):String
}