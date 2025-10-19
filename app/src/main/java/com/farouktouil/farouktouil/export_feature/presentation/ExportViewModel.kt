package com.farouktouil.farouktouil.export_feature.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.farouktouil.farouktouil.core.util.Resource
import com.farouktouil.farouktouil.export_feature.domain.repository.ExportRepository

import com.farouktouil.farouktouil.export_feature.presentation.state.FileExportState
import com.farouktouil.farouktouil.order_feature.domain.repository.OrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class ExportViewModel @Inject constructor(
    private val exportRepository: ExportRepository,
    private val orderRepository: OrderRepository  // Fetch orders from DB
) : ViewModel() {

    var collectedDataAmount by mutableStateOf(0)
        private set

    var fileExportState by mutableStateOf(FileExportState())
        private set

    var selectedDelivererName by mutableStateOf<String?>(null)
        private set

    var availableDeliverers by mutableStateOf<List<String>>(emptyList())
        private set

    fun generateExportFile() {
        viewModelScope.launch {
            val orders = orderRepository.getOrders() // Fetch all orders from DB

            if (orders.isEmpty()) {
                fileExportState = fileExportState.copy(failedGenerating = true)
                return@launch
            }

            // Filter orders by selected deliverer if one is selected
            val filteredOrders = if (selectedDelivererName != null) {
                orders.filter { order ->
                    order.delivererName == selectedDelivererName
                }
            } else {
                orders
            }

            if (filteredOrders.isEmpty()) {
                fileExportState = fileExportState.copy(failedGenerating = true)
                return@launch
            }

            // Count total products for collectedDataAmount (filtered by selected deliverer)
            collectedDataAmount = if (selectedDelivererName != null) {
                filteredOrders.sumOf { it.products.size }
            } else {
                filteredOrders.sumOf { it.products.size }
            }

            fileExportState = fileExportState.copy(isGeneratingLoading = true)

            exportRepository.startExportData(filteredOrders)  // Pass filtered orders for export
                .onEach { pathInfo ->
                    when (pathInfo) {
                        is Resource.Success -> {
                            fileExportState = fileExportState.copy(
                                isSharedDataReady = true,
                                isGeneratingLoading = false,
                                shareDataUri = pathInfo.data.path,
                                generatingProgress = 100
                            )
                        }

                        is Resource.Loading -> {
                            pathInfo.data?.let {
                                fileExportState = fileExportState.copy(
                                    generatingProgress = pathInfo.data.progressPercentage
                                )
                            }
                        }

                        is Resource.Error -> {
                            fileExportState = fileExportState.copy(
                                failedGenerating = true,
                                isGeneratingLoading = false
                            )
                        }
                    }
                }.launchIn(viewModelScope)
        }
    }

    fun setSelectedDeliverer(delivererName: String?) {
        selectedDelivererName = delivererName
    }

    fun loadAvailableDeliverers() {
        viewModelScope.launch {
            val orders = orderRepository.getOrders()
            val deliverers = orders.map { it.delivererName }
                .distinct()
                .sorted()
            availableDeliverers = deliverers
        }
    }

    fun onShareDataClick() {
        fileExportState = fileExportState.copy(isShareDataClicked = true)
    }

    fun onShareDataOpen() {
        fileExportState = fileExportState.copy(isShareDataClicked = false)
    }
}
