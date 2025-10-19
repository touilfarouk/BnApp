package com.farouktouil.farouktouil.order_feature.presentation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.farouktouil.farouktouil.deliverer_feature.domain.repository.DelivererRepository
import com.farouktouil.farouktouil.order_feature.domain.model.Order
import com.farouktouil.farouktouil.order_feature.domain.repository.OrderRepository
import com.farouktouil.farouktouil.order_feature.presentation.mapper.toOrderDetailListItem
import com.farouktouil.farouktouil.order_feature.presentation.mapper.toOrderListItem
import com.farouktouil.farouktouil.order_feature.presentation.state.OrderDetailListItem
import com.farouktouil.farouktouil.order_feature.presentation.state.OrderListItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class OrderViewModel @Inject constructor(
    private val orderRepository: OrderRepository
   //private val delivererRepository: DelivererRepository
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
           orders = orderRepository.getOrders() // Ensure orders are fetched
            setupOrderList()

            // Insert test order
           /*orderRepository.insertOrder(
                Order(
                    orderId = 1,
                    date = "2022.10.15 12:05:12",
                    delivererTime = "As fast as possible",
                    delivererName = "Paper Factory Ltd",
                    products = listOf(
                        BoughtProduct(
                            productId = 1,
                            name = "Notebook",
                            pricePerAmount = 1.23f,
                            amount = 2
                        )
                    )
                )
            )*/
            //delivererRepository.insertDeliverers(DummyData.deliverers)
        }
    }

    fun deleteOrder(orderId: String) {
        viewModelScope.launch {
            orderRepository.deleteOrder(orderId)
            orders = orderRepository.getOrders() // Refresh order list
            setupOrderList()
        }
    }


    fun onOrderClick(orderId: String) {
        initOrderForDialog(orderId)
        isOrderDialogShown = true
    }

    private fun initOrderForDialog(orderId: String) {
        clickedOrderItem = orders.firstOrNull { it.orderId == orderId.toString() }?.toOrderDetailListItem()
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
