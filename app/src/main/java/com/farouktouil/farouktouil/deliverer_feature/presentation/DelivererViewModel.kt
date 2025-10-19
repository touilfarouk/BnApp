package com.farouktouil.farouktouil.deliverer_feature.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.farouktouil.farouktouil.core.domain.model.Deliverer
import com.farouktouil.farouktouil.deliverer_feature.domain.useCase.DeleteDelivererUseCase
import com.farouktouil.farouktouil.deliverer_feature.domain.useCase.GetAllDeliverersUseCase
import com.farouktouil.farouktouil.deliverer_feature.domain.useCase.InsertDelivererUseCase
import com.farouktouil.farouktouil.deliverer_feature.domain.useCase.RefreshDeliverersUseCase
import com.farouktouil.farouktouil.deliverer_feature.domain.useCase.UpdateDelivererUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn


import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DelivererViewModel @Inject constructor(
    private val insertDelivererUseCase: InsertDelivererUseCase,
    private val deleteDelivererUseCase: DeleteDelivererUseCase,
    private val updateDelivererUseCase: UpdateDelivererUseCase,
    private val getAllDeliverersUseCase: GetAllDeliverersUseCase,
    private val refreshDeliverersUseCase: RefreshDeliverersUseCase
) : ViewModel() {

    init {
        viewModelScope.launch {
            refreshDeliverersUseCase()
        }
    }

    val uiState =
        getAllDeliverersUseCase.invoke()
            .map { DelivererUiState(it) }
            .stateIn(viewModelScope, SharingStarted.Eagerly, DelivererUiState())

    fun insert(name: String) = viewModelScope.launch {
        val deliverer = Deliverer(name = name)
        insertDelivererUseCase.invoke(deliverer)
    }
    fun update(deliverer: Deliverer) = viewModelScope.launch {
        updateDelivererUseCase.invoke(deliverer)
    }

    fun delete(deliverer: Deliverer) = viewModelScope.launch {
        deleteDelivererUseCase.invoke(deliverer)
    }

}


data class DelivererUiState(
    val data: List<Deliverer> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
