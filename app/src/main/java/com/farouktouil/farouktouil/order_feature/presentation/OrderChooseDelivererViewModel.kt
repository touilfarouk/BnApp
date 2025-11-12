package com.farouktouil.farouktouil.order_feature.presentation


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.farouktouil.farouktouil.core.domain.model.Structure
import com.farouktouil.farouktouil.order_feature.domain.repository.OrderRepository
import com.farouktouil.farouktouil.order_feature.domain.use_case.FilterListByNameUseCase
import com.farouktouil.farouktouil.order_feature.domain.use_case.SortListByNameUseCase
import com.farouktouil.farouktouil.order_feature.presentation.mapper.toStructureListItem
import com.farouktouil.farouktouil.order_feature.presentation.state.StructureListItem
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
class OrderChooseStructureViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
    private val sortListByNameUseCase: SortListByNameUseCase,
    private val filterListByNameUseCase: FilterListByNameUseCase
) : ViewModel() {

    private val _structuresToShow = MutableStateFlow<List<StructureListItem>>(emptyList())
    val structuresToShow: StateFlow<List<StructureListItem>> = _structuresToShow

    private val _structureSearchQuery = MutableStateFlow("")
    val structureSearchQuery: StateFlow<String> = _structureSearchQuery

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val structuresFlow: Flow<List<Structure>> = orderRepository.getStructures()

    init {
        viewModelScope.launch {
            structuresFlow
                .combine(_structureSearchQuery.debounce(300)) { structures, query ->
                    filterListByNameUseCase(
                        sortListByNameUseCase(structures),
                        query
                    ).map { structure ->
                        structure.toStructureListItem()
                    }
                }
                .catch { e ->
                    _errorMessage.value = "Error fetching structures: ${e.message}"
                }
                .collect { filteredStructures ->
                    _structuresToShow.value = filteredStructures
                    _isLoading.value = false
                }
        }
    }

    fun onSearchQueryChange(newValue: String) {
        _structureSearchQuery.value = newValue
    }
}