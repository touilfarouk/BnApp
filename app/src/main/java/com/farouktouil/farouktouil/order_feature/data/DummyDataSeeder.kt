package com.farouktouil.farouktouil.order_feature.data

import com.farouktouil.farouktouil.core.data.local.ProductDao
import com.farouktouil.farouktouil.core.data.local.entities.ProductEntity
import com.farouktouil.farouktouil.order_feature.domain.model.BoughtProduct
import com.farouktouil.farouktouil.order_feature.domain.model.Order
import com.farouktouil.farouktouil.order_feature.domain.repository.OrderRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DummyDataSeeder @Inject constructor(
    private val productDao: ProductDao,
    private val orderRepository: OrderRepository
) {

    private val formatter = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault())

    suspend fun seedIfEmpty() = withContext(Dispatchers.IO) {
        val existingOrders = orderRepository.getOrders()
        if (existingOrders.isNotEmpty()) {
            return@withContext
        }

        val structureName = "Structure Démo"
        val demoProducts = listOf(
            ProductEntity(
                productId = 9001,
                name = "Ordinateur Portable",
                label = "Dell Latitude 7420",
                pricePerAmount = 245000f,
                quantity = 12,
                minQuantity = 2,
                maxQuantity = 20,
                structureName = structureName,
                assignedPersonnelId = 101,
                assignedPersonnelName = "Nadia Ben"
            ),
            ProductEntity(
                productId = 9002,
                name = "Écran 27\"",
                label = "Samsung S27F",
                pricePerAmount = 68000f,
                quantity = 15,
                minQuantity = 3,
                maxQuantity = 25,
                structureName = structureName,
                assignedPersonnelId = 102,
                assignedPersonnelName = "Yacine Kaci"
            )
        )

        demoProducts.forEach { productDao.insertProduct(it) }

        val timestamp = formatter.format(Date())
        val boughtProducts = listOf(
            BoughtProduct(
                productId = 9001,
                name = "Ordinateur Portable",
                label = "Dell Latitude 7420",
                pricePerAmount = 245000f,
                amount = 2,
                structureName = structureName,
                assignedPersonnelName = "Nadia Ben"
            ),
            BoughtProduct(
                productId = 9002,
                name = "Écran 27\"",
                label = "Samsung S27F",
                pricePerAmount = 68000f,
                amount = 1,
                structureName = structureName,
                assignedPersonnelName = "Yacine Kaci"
            )
        )

        val demoOrder = Order(
            orderId = UUID.randomUUID().toString(),
            date = timestamp,
            checkoutTime = timestamp,
            structureName = structureName,
            products = boughtProducts,
            personnelNames = listOf("Nadia Ben", "Yacine Kaci")
        )

        orderRepository.insertOrder(demoOrder)
    }
}
