package com.farouktouil.farouktouil.order_feature.domain.use_case

import android.annotation.SuppressLint
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
    private val orderRepository: OrderRepository
) {

    @SuppressLint("NewApi")
    suspend operator fun invoke(products: List<BoughtProduct>, structureName: String) {
        // Validate input
        if (products.isEmpty()) {
            throw IllegalArgumentException("No products selected for order")
        }

        if (structureName.isBlank()) {
            throw IllegalArgumentException("Invalid structure selected")
        }

        return withContext(Dispatchers.IO) {
            val timestamp = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss").format(LocalDateTime.now())
            val order = Order(
                orderId = UUID.randomUUID().toString(),
                date = timestamp,
                checkoutTime = timestamp,
                structureName = structureName,
                products = products
            )

            orderRepository.insertOrder(order)
        }
    }
}