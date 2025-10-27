package com.farouktouil.farouktouil.consultation_feature.presentation

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.farouktouil.farouktouil.consultation_feature.domain.model.AppelConsultation
import com.farouktouil.farouktouil.consultation_feature.domain.use_case.GetConsultationCallsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConsultationViewModel @Inject constructor(
    private val getConsultationCallsUseCase: GetConsultationCallsUseCase
) : ViewModel() {

    private val _state = mutableStateOf(ConsultationState())
    val state: State<ConsultationState> = _state

    init {
        loadConsultationCalls()
    }

    fun onEvent(event: ConsultationEvent) {
        when (event) {
            is ConsultationEvent.RefreshConsultations -> {
                loadConsultationCalls()
            }
        }
    }

    private fun loadConsultationCalls() {
        _state.value = _state.value.copy(isLoading = true, error = null)

        viewModelScope.launch {
            getConsultationCallsUseCase()
                .onSuccess { consultations ->
                    _state.value = _state.value.copy(
                        consultations = consultations,
                        isLoading = false
                    )
                }
                .onFailure { error ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = error.message ?: "Erreur lors du chargement des appels à consultation"
                    )
                }
        }
    }
}

data class ConsultationState(
    val consultations: List<AppelConsultation> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed class ConsultationEvent {
    object RefreshConsultations : ConsultationEvent()
}
