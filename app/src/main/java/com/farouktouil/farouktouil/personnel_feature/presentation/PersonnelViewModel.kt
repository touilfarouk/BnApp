package com.farouktouil.farouktouil.personnel_feature.presentation

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.farouktouil.farouktouil.personnel_feature.domain.model.Personnel
import com.farouktouil.farouktouil.personnel_feature.domain.use_case.GetPersonnelUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PersonnelViewModel @Inject constructor(
    private val getPersonnelUseCase: GetPersonnelUseCase
) : ViewModel() {

    private val _state = mutableStateOf(PersonnelState())
    val state: State<PersonnelState> = _state

    init {
        loadPersonnel()
    }

    fun onEvent(event: PersonnelEvent) {
        when (event) {
            is PersonnelEvent.RefreshPersonnel -> {
                loadPersonnel()
            }
        }
    }

    private fun loadPersonnel() {
        _state.value = _state.value.copy(isLoadingPersonnel = true, personnelError = null)

        viewModelScope.launch {
            getPersonnelUseCase()
                .onSuccess { personnelList ->
                    _state.value = _state.value.copy(
                        personnel = personnelList,
                        isLoadingPersonnel = false
                    )
                }
                .onFailure { error ->
                    _state.value = _state.value.copy(
                        isLoadingPersonnel = false,
                        personnelError = error.message ?: "Failed to load personnel"
                    )
                }
        }
    }
}

data class PersonnelState(
    val personnel: List<Personnel> = emptyList(),
    val isLoadingPersonnel: Boolean = false,
    val personnelError: String? = null
)

sealed class PersonnelEvent {
    object RefreshPersonnel : PersonnelEvent()
}
