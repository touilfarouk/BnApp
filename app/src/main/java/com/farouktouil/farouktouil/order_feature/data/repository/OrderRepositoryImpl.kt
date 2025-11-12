package com.farouktouil.farouktouil.order_feature.data.repository

import com.farouktouil.farouktouil.core.data.local.OrderDao
import com.farouktouil.farouktouil.core.data.local.ProductAccessoryDao
import com.farouktouil.farouktouil.core.data.local.ProductDao
import com.farouktouil.farouktouil.core.data.local.entities.OrderProductEntity
import com.farouktouil.farouktouil.core.domain.model.AccessoryType
import com.farouktouil.farouktouil.core.domain.model.Product
import com.farouktouil.farouktouil.core.domain.model.Structure
import com.farouktouil.farouktouil.order_feature.data.mapper.toOrder
import com.farouktouil.farouktouil.order_feature.data.mapper.toOrderEntity
import com.farouktouil.farouktouil.order_feature.data.mapper.toEntity
import com.farouktouil.farouktouil.order_feature.data.mapper.toSelection
import com.farouktouil.farouktouil.order_feature.domain.model.Order
import com.farouktouil.farouktouil.order_feature.domain.model.ProductAccessorySelection
import com.farouktouil.farouktouil.order_feature.domain.repository.OrderRepository
import com.farouktouil.farouktouil.product_feature.data.mapper.toProduct
import com.farouktouil.farouktouil.personnel_feature.data.local.dao.PersonnelDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class OrderRepositoryImpl @Inject constructor(
    private val orderDao:OrderDao,
    private val productDao: ProductDao,
    private val productAccessoryDao: ProductAccessoryDao,
    private val personnelDao: PersonnelDao
):OrderRepository {
    override suspend fun insertOrder(order: Order) {
        orderDao.insertOrder(order.toOrderEntity())
        val orderProductEntities = order.products.map { boughtProduct ->
            OrderProductEntity(order.orderId,boughtProduct.productId, boughtProduct.amount)
        }
        orderDao.insertOrderProductEntities(orderProductEntities)
    }

    override suspend fun getOrders(): List<Order> {
        return orderDao.getOrderWithProducts().map {
            it.toOrder()
        }
    }


    override fun getStructures(): Flow<List<Structure>> {
        return personnelDao.getDistinctStructures()
            .map { names ->
                names.filter { it.isNotBlank() }
                    .map { Structure(name = it) }
            }
    }

    override fun getProductsForStructure(structureName: String): Flow<List<Product>> {
        return productDao.getProductsForStructure(structureName)
            .map { productEntities -> // List<ProductEntity>
                productEntities.map { productEntity ->
                    productEntity.toProduct() // Convert ProductEntity to Product
                }
            }
    }

    override fun observeProductAccessories(): Flow<List<ProductAccessorySelection>> {
        return productAccessoryDao.observeAll()
            .map { entities -> entities.map { it.toSelection() } }
    }

    override suspend fun updateProductAccessories(productId: Int, selectedTypes: Set<AccessoryType>) {
        if (selectedTypes.isEmpty()) {
            productAccessoryDao.deleteForProduct(productId)
        } else {
            productAccessoryDao.upsert(selectedTypes.toEntity(productId))
        }
    }

    override suspend fun deleteOrder(orderId: String) {
        orderDao.deleteOrderProducts(orderId) // Delete associated products first
        orderDao.deleteOrder(orderId) // Then delete the order itself
    }

}