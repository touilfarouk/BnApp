package com.farouktouil.farouktouil.consultation_feature.presentation

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material3.Icon
import androidx.compose.ui.unit.dp
import android.util.Log
import kotlin.math.min
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
    Log.d("ConsultationList", "Recomposing with ${consultations.itemCount} items, loadState: ${consultations.loadState}")
    
    LaunchedEffect(consultations.loadState) {
        Log.d("ConsultationList", "Load state changed: ${consultations.loadState}")
        Log.d("ConsultationList", "Items in paging data: ${consultations.itemCount}")
        
        // Log first few items for debugging
        val items = (0 until minOf(3, consultations.itemCount)).mapNotNull { consultations[it] }
        Log.d("ConsultationList", "First ${items.size} items: $items")
    }

    LazyColumn(contentPadding = PaddingValues(vertical = 8.dp)) {
        when (val loadState = consultations.loadState.refresh) {
            is LoadState.Loading -> {
                item {
                    Log.d("ConsultationList", "Showing loading state")
                    Box(modifier = Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            }
            is LoadState.Error -> {
                item {
                    Log.e("ConsultationList", "Error loading data", loadState.error)
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
                Log.d("ConsultationList", "Displaying ${consultations.itemCount} items")
                if (consultations.itemCount == 0) {
                    item {
                        Log.d("ConsultationList", "No items to display")
                        Box(modifier = Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No consultations found")
                        }
                    }
                } else {
                    items(
                        count = consultations.itemCount,
                        key = { index ->
                            val item = consultations.peek(index)
                            Log.d("ConsultationList", "Item at $index: ${item?.id} - ${item?.displayTitle}")
                            item?.id ?: index
                        }
                    ) { index ->
                        val consultation = consultations[index]
                        if (consultation != null) {
                            Log.d("ConsultationList", "Rendering item at $index: ${consultation.id} - ${consultation.displayTitle}")
                            ConsultationItem(consultation = consultation)
                        } else {
                            Log.d("ConsultationList", "Null consultation at index $index")
                        }
                    }
                }
            }
        }

        when (val loadState = consultations.loadState.append) {
            is LoadState.Loading -> {
                item {
                    Log.d("ConsultationList", "Loading more items...")
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
            is LoadState.Error -> {
                item {
                    Log.e("ConsultationList", "Error appending data", loadState.error)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "Error loading more: ${loadState.error.message}")
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConsultationItem(
    consultation: AppelConsultation,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        onClick = onClick,
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Title with proper styling
            Text(
                text = consultation.displayTitle,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // ID and Date row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // ID
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Fingerprint,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "#${consultation.id}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                // Date
                if (!consultation.displayDate.isNullOrBlank()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = consultation.displayDate,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
