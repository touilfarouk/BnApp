package com.farouktouil.farouktouil.order_feature.presentation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.farouktouil.farouktouil.order_feature.data.DummyDataSeeder
import com.farouktouil.farouktouil.order_feature.domain.model.Order
import com.farouktouil.farouktouil.order_feature.domain.repository.OrderRepository
import com.farouktouil.farouktouil.order_feature.presentation.mapper.toOrderDetailListItem
import com.farouktouil.farouktouil.order_feature.presentation.mapper.toOrderListItem
import com.farouktouil.farouktouil.order_feature.presentation.state.OrderDetailListItem
import com.farouktouil.farouktouil.order_feature.presentation.state.OrderListItem
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class OrderViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
    private val dummyDataSeeder: DummyDataSeeder
) : ViewModel() {

    private var orders: List<Order> = emptyList()

    var orderList by mutableStateOf<List<OrderListItem>>(emptyList())
        private set

    var isOrderDialogShown by mutableStateOf(false)
        private set

    var clickedOrderItem by mutableStateOf<OrderDetailListItem?>(null)
        private set

    init {
        viewModelScope.launch {
            orderRepository.observeProductAccessories()
                .map { selections -> selections.associate { it.productId to it.selectedTypes } }
                .collectLatest { currentAccessoriesByProduct ->
                    accessoriesByProduct = currentAccessoriesByProduct
                    loadOrders()
                }
        }
        refreshOrders()
    }

    private var accessoriesByProduct: Map<Int, Set<com.farouktouil.farouktouil.core.domain.model.AccessoryType>> = emptyMap()

    fun refreshOrders() {
        viewModelScope.launch {
            dummyDataSeeder.seedIfEmpty()
            loadOrders()
        }
    }

    private suspend fun loadOrders() {
        orders = orderRepository.getOrders()
        setupOrderList()
        initOrderForDialog(clickedOrderItem?.orderId)
    }

    fun deleteOrder(orderId: String) {
        viewModelScope.launch {
            orderRepository.deleteOrder(orderId)
            loadOrders()
        }
    }


    fun onOrderClick(orderId: String) {
        initOrderForDialog(orderId)
        isOrderDialogShown = true
    }

    private fun initOrderForDialog(orderId: String?) {
        if (orderId == null) {
            clickedOrderItem = null
            return
        }
        clickedOrderItem = orders.firstOrNull { it.orderId == orderId }?.toOrderDetailListItem()
    }

    fun onDismissOrderDialog() {
        isOrderDialogShown = false
        clickedOrderItem = null
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupOrderList() {
        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")

        orderList = orders.map { order ->
            order.toOrderListItem()
        }.sortedByDescending { orderListItem ->
            LocalDateTime.parse(orderListItem.orderDate, formatter)
        }
    }
}
