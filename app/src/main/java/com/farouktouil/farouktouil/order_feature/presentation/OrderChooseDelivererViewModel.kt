package com.farouktouil.farouktouil.order_feature.presentation


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.farouktouil.farouktouil.core.domain.model.Deliverer
import com.farouktouil.farouktouil.order_feature.domain.repository.OrderRepository
import com.farouktouil.farouktouil.order_feature.domain.use_case.FilterListByNameUseCase
import com.farouktouil.farouktouil.order_feature.domain.use_case.SortListByNameUseCase
import com.farouktouil.farouktouil.order_feature.presentation.mapper.toDelivererListItem
import com.farouktouil.farouktouil.order_feature.presentation.state.DelivererListItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderChooseDelivererViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
    private val sortListByNameUseCase: SortListByNameUseCase,
    private val filterListByNameUseCase: FilterListByNameUseCase
) : ViewModel() {

    // StateFlow for the list of deliverers to display
    private val _deliverersToShow = MutableStateFlow<List<DelivererListItem>>(emptyList())
    val deliverersToShow: StateFlow<List<DelivererListItem>> = _deliverersToShow

    // StateFlow for the search query
    private val _delivererSearchQuery = MutableStateFlow("")
    val delivererSearchQuery: StateFlow<String> = _delivererSearchQuery

    // StateFlow for loading state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // StateFlow for error messages
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    // Collect the Flow from the repository
    private val deliverersFlow: Flow<List<Deliverer>> = orderRepository.getDeliverers()

    init {
        // Observe the Flow and update deliverersToShow whenever the data changes
        viewModelScope.launch {
            deliverersFlow
                .combine(_delivererSearchQuery.debounce(300)) { deliverers, query ->
                    // Filter and sort the deliverers based on the search query
                    filterListByNameUseCase(
                        sortListByNameUseCase(deliverers),
                        query
                    ).map { deliverer ->
                        deliverer.toDelivererListItem()
                    }
                }
                .catch { e ->
                    _errorMessage.value = "Error fetching deliverers: ${e.message}"
                }
                .collect { filteredDeliverers ->
                    _deliverersToShow.value = filteredDeliverers
                    _isLoading.value = false
                }
        }
    }

    // Update the search query and emit it to the StateFlow
    fun onSearchQueryChange(newValue: String) {
        _delivererSearchQuery.value = newValue
    }
}