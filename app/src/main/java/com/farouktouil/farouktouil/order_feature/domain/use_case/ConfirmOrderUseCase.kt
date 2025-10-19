package com.farouktouil.farouktouil.order_feature.domain.use_case

import android.annotation.SuppressLint
import com.farouktouil.farouktouil.core.data.local.ProductDao
import com.farouktouil.farouktouil.order_feature.domain.model.BoughtProduct
import com.farouktouil.farouktouil.order_feature.domain.model.Order
import com.farouktouil.farouktouil.order_feature.domain.repository.OrderRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID
import javax.inject.Inject

class ConfirmOrderUseCase @Inject constructor(
    private val orderRepository: OrderRepository,
    private val productDao: ProductDao
) {

    @SuppressLint("NewApi")
    suspend operator fun invoke(products: List<BoughtProduct>, delivererId: Int) {
        // Validate input
        if (products.isEmpty()) {
            throw IllegalArgumentException("No products selected for order")
        }

        if (delivererId <= 0) {
            throw IllegalArgumentException("Invalid deliverer selected")
        }

        return withContext(Dispatchers.IO) {
            try {
                // Check if all products have sufficient stock before creating order
                for (product in products) {
                    if (product.productId <= 0) {
                        throw IllegalArgumentException("Invalid product ID: ${product.productId}")
                    }

                    val productEntity = productDao.getProductById(product.productId)
                        ?: throw IllegalArgumentException("Product not found: ${product.productId}")

                    if (productEntity.quantity < product.amount) {
                        throw IllegalArgumentException("Insufficient stock for product: ${product.name}. Available: ${productEntity.quantity}, Requested: ${product.amount}")
                    }
                }

                // Get deliverer name
                val delivererName = orderRepository.getDelivererNameById(delivererId)
                    ?: throw IllegalArgumentException("Deliverer not found with ID: $delivererId")

                // Create and insert the order
                val order = Order(
                    orderId = UUID.randomUUID().toString(),
                    date = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss").format(LocalDateTime.now()),
                    delivererTime = "As fast as possible",
                    delivererName = delivererName,
                    products = products
                )

                orderRepository.insertOrder(order)

                // Subtract quantities from product stock after successful order creation
                for (product in products) {
                    productDao.adjustProductQuantity(product.productId, -product.amount)
                }

            } catch (e: Exception) {
                // Handle any errors during order creation or stock adjustment
                throw e
            }
        }
    }
}