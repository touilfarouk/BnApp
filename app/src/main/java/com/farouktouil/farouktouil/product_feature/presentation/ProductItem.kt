package com.farouktouil.farouktouil.product_feature.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.farouktouil.farouktouil.core.data.local.entities.DelivererEntity
import com.farouktouil.farouktouil.core.domain.model.Product

@Composable
fun ProductItem(
    product: Product,
    deliverers: List<DelivererEntity>,
    onDelete: () -> Unit
) {
    // Find the deliverer name by ID
    val delivererName = deliverers.find { it.delivererId == product.belongsToDeliverer.toInt() }?.name ?: "Unknown"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { /* Handle click */ }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Product: ${product.name}", style = MaterialTheme.typography.bodySmall)
            Text(text = "Price: ${product.pricePerAmount}", style = MaterialTheme.typography.bodySmall)
            Text(text = "Deliverer: $delivererName (ID: ${product.belongsToDeliverer})", style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(8.dp))
            IconButton(onClick = onDelete) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete Product")
            }
        }
    }
}