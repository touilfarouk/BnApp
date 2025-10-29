package com.farouktouil.farouktouil.consultation_feature.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.farouktouil.farouktouil.consultation_feature.domain.model.AppelConsultation
import com.farouktouil.farouktouil.consultation_feature.domain.repository.ConsultationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SaveConsultationViewModel @Inject constructor(
    private val repository: ConsultationRepository
) : ViewModel() {

    private val _saveState = MutableStateFlow<SaveConsultationState>(SaveConsultationState.Idle)
    val saveState: StateFlow<SaveConsultationState> = _saveState.asStateFlow()

    fun saveConsultation(consultation: AppelConsultation) {
        viewModelScope.launch {
            _saveState.value = SaveConsultationState.Loading
            try {
                val id = repository.saveConsultation(consultation)
                _saveState.value = SaveConsultationState.Success(id)
            } catch (e: Exception) {
                _saveState.value = SaveConsultationState.Error(e.message ?: "Failed to save consultation")
            }
        }
    }

    fun resetState() {
        _saveState.value = SaveConsultationState.Idle
    }
}

sealed class SaveConsultationState {
    object Idle : SaveConsultationState()
    object Loading : SaveConsultationState()
    data class Success(val id: Long) : SaveConsultationState()
    data class Error(val message: String) : SaveConsultationState()
}
