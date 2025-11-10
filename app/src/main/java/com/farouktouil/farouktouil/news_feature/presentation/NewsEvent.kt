package com.farouktouil.farouktouil.news_feature.presentation

sealed class NewsEvent {
    data class OnSearchQueryChanged(val value: String) : NewsEvent()
}
