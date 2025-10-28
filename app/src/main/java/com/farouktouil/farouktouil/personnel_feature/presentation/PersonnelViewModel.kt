package com.farouktouil.farouktouil.personnel_feature.presentation

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.farouktouil.farouktouil.personnel_feature.domain.model.Personnel
import com.farouktouil.farouktouil.personnel_feature.domain.model.PersonnelSearchQuery
import com.farouktouil.farouktouil.personnel_feature.domain.use_case.GetPersonnelUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class PersonnelViewModel @Inject constructor(
    private val getPersonnelUseCase: GetPersonnelUseCase
) : ViewModel() {

    private val _state = mutableStateOf(PersonnelScreenState())
    val state: State<PersonnelScreenState> = _state

    private val _searchQuery = MutableStateFlow(PersonnelSearchQuery())

    val personnel: Flow<PagingData<Personnel>> = _searchQuery.flatMapLatest {
        getPersonnelUseCase(it)
    }.cachedIn(viewModelScope)

    fun onEvent(event: PersonnelEvent) {
        when (event) {
            is PersonnelEvent.OnNameQueryChange -> {
                _state.value = _state.value.copy(nameQuery = event.query)
                _searchQuery.value = _searchQuery.value.copy(name = event.query)
            }
            is PersonnelEvent.OnStructureQueryChange -> {
                _state.value = _state.value.copy(structureQuery = event.query)
                _searchQuery.value = _searchQuery.value.copy(structure = event.query)
            }
            is PersonnelEvent.OnActiveStatusChange -> {
                _state.value = _state.value.copy(activeStatus = event.active)
                _searchQuery.value = _searchQuery.value.copy(active = event.active)
            }
        }
    }
}

data class PersonnelScreenState(
    val nameQuery: String = "",
    val structureQuery: String = "",
    val activeStatus: Int? = null // null for all, 1 for active, 0 for inactive
)

sealed class PersonnelEvent {
    data class OnNameQueryChange(val query: String) : PersonnelEvent()
    data class OnStructureQueryChange(val query: String) : PersonnelEvent()
    data class OnActiveStatusChange(val active: Int?) : PersonnelEvent()
}
