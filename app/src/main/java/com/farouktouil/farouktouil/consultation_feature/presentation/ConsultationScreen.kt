package com.farouktouil.farouktouil.consultation_feature.presentation

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.farouktouil.farouktouil.consultation_feature.domain.model.AppelConsultation

@SuppressLint("StateFlowValueCalledInComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConsultationScreen(modifier: Modifier = Modifier, viewModel: ConsultationViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()
    val consultations = viewModel.consultations.collectAsLazyPagingItems()

    Column(modifier = modifier.padding(16.dp)) {
        SearchAndFilterCard(state = state, onEvent = viewModel::onEvent)
        Spacer(modifier = Modifier.height(16.dp))
        ConsultationList(consultations = consultations)
    }
}

@Composable
fun SearchAndFilterCard(state: ConsultationScreenState, onEvent: (ConsultationEvent) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Button(onClick = { expanded = !expanded }) {
                Text(if (expanded) "Hide Search & Filter" else "Show Search & Filter")
            }
            if (expanded) {
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = state.nomAppelConsultation,
                    onValueChange = { onEvent(ConsultationEvent.OnNomAppelConsultationChanged(it)) },
                    label = { Text("Search by Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = state.dateDepot,
                    onValueChange = { onEvent(ConsultationEvent.OnDateDepotChanged(it)) },
                    label = { Text("Search by Date") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun ConsultationList(consultations: LazyPagingItems<AppelConsultation>) {
    LazyColumn(contentPadding = PaddingValues(vertical = 8.dp)) {
        when (val loadState = consultations.loadState.refresh) {
            is LoadState.Loading -> {
                item {
                    Box(modifier = Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            }
            is LoadState.Error -> {
                item {
                    Box(modifier = Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "Error: ${loadState.error.message}")
                            Button(onClick = { consultations.retry() }) {
                                Text(text = "Retry")
                            }
                        }
                    }
                }
            }
            else -> {
                items(consultations.itemCount, key = { index -> consultations.peek(index)?.id ?: index }) {
                    consultations[it]?.let { consultation ->
                        ConsultationItem(consultation = consultation)
                    }
                }
            }
        }

        when (val loadState = consultations.loadState.append) {
            is LoadState.Loading -> {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(8.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            }
            is LoadState.Error -> {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(8.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "Error: ${loadState.error.message}")
                            Button(onClick = { consultations.retry() }) {
                                Text(text = "Retry")
                            }
                        }
                    }
                }
            }
            else -> {}
        }
    }
}

@Composable
fun ConsultationItem(consultation: AppelConsultation) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Nom: ${consultation.nom_appel_consultation ?: "N/A"}")
            Text(text = "Date dépôt: ${consultation.date_depot ?: "N/A"}")
            Text(text = "Clé: ${consultation.cle_appel_consultation ?: "N/A"}")
        }
    }
}
