package com.farouktouil.farouktouil.order_feature.data.repository

import com.farouktouil.farouktouil.core.data.local.DelivererDao
import com.farouktouil.farouktouil.core.data.local.OrderDao
import com.farouktouil.farouktouil.core.data.local.ProductDao
import com.farouktouil.farouktouil.core.data.local.entities.OrderProductEntity


import com.farouktouil.farouktouil.core.domain.model.Deliverer
import com.farouktouil.farouktouil.core.domain.model.Product
import com.farouktouil.farouktouil.deliverer_feature.data.mapper.toDeliverer
import com.farouktouil.farouktouil.order_feature.data.mapper.toOrder
import com.farouktouil.farouktouil.order_feature.data.mapper.toOrderEntity
import com.farouktouil.farouktouil.order_feature.domain.model.Order
import com.farouktouil.farouktouil.order_feature.domain.repository.OrderRepository
import com.farouktouil.farouktouil.product_feature.data.mapper.toProduct
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class OrderRepositoryImpl @Inject constructor(
    private val orderDao:OrderDao,
    private val delivererDao: DelivererDao,
    private val productDao: ProductDao
):OrderRepository {
    override suspend fun insertOrder(order: Order) {
        orderDao.insertOrder(order.toOrderEntity(order.delivererName))
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


    override fun getDeliverers(): Flow<List<Deliverer>> {
        return delivererDao.getDeliverers()
            .map { delivererWithProductsList -> // List<DelivererWithProducts>
                delivererWithProductsList.map { delivererWithProducts -> // DelivererWithProducts
                    delivererWithProducts.delivererEntity.toDeliverer(
                        delivererWithProducts.products.map { productEntity ->
                            productEntity.toProduct()
                        }
                    )
                }
            }
    }

    override fun getProductsForDeliverer(delivererId: Int): Flow<List<Product>> {
        return productDao.getProductsForDeliverer(delivererId)
            .map { productEntities -> // List<ProductEntity>
                productEntities.map { productEntity ->
                    productEntity.toProduct() // Convert ProductEntity to Product
                }
            }
    }

    override suspend fun getDelivererNameById(delivererId: Int): String {
        return delivererDao.getDelivererNameById(delivererId)
    }

    override suspend fun deleteOrder(orderId: String) {
        orderDao.deleteOrderProducts(orderId) // Delete associated products first
        orderDao.deleteOrder(orderId) // Then delete the order itself
    }

}