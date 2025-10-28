package com.farouktouil.farouktouil.consultation_feature.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.farouktouil.farouktouil.consultation_feature.domain.model.AppelConsultation
import com.farouktouil.farouktouil.consultation_feature.domain.model.ConsultationSearchQuery
import com.farouktouil.farouktouil.consultation_feature.domain.use_case.GetConsultationCallsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class ConsultationViewModel @Inject constructor(
    private val getConsultationCallsUseCase: GetConsultationCallsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ConsultationScreenState())
    val state = _state.asStateFlow()

    val consultations: Flow<PagingData<AppelConsultation>> = _state
        .debounce(500L)
        .flatMapLatest { currentState ->
            getConsultationCallsUseCase(
                ConsultationSearchQuery(
                    nom_appel_consultation = currentState.nomAppelConsultation,
                    date_depot = currentState.dateDepot
                )
            )
        }
        .cachedIn(viewModelScope)

    fun onEvent(event: ConsultationEvent) {
        when (event) {
            is ConsultationEvent.OnNomAppelConsultationChanged -> {
                _state.update { it.copy(nomAppelConsultation = event.value) }
            }
            is ConsultationEvent.OnDateDepotChanged -> {
                _state.update { it.copy(dateDepot = event.value) }
            }
        }
    }
}
