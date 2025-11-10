package com.farouktouil.farouktouil.news_feature.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.farouktouil.farouktouil.news_feature.domain.model.NewsArticle
import com.farouktouil.farouktouil.news_feature.domain.model.NewsSearchQuery
import com.farouktouil.farouktouil.news_feature.domain.use_case.GetNewsUseCase
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
class NewsViewModel @Inject constructor(
    private val getNewsUseCase: GetNewsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(NewsScreenState())
    val state = _state.asStateFlow()

    val news: Flow<PagingData<NewsArticle>> = _state
        .debounce(400L)
        .flatMapLatest { currentState ->
            getNewsUseCase(
                NewsSearchQuery(keyword = currentState.searchQuery)
            )
        }
        .cachedIn(viewModelScope)

    fun onEvent(event: NewsEvent) {
        when (event) {
            is NewsEvent.OnSearchQueryChanged -> {
                _state.update { it.copy(searchQuery = event.value) }
            }
        }
    }
}
