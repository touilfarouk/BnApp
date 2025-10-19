package com.farouktouil.farouktouil.order_feature.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert

import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.farouktouil.farouktouil.export_feature.presentation.ExportScreen
import com.farouktouil.farouktouil.order_feature.presentation.state.OrderListItem
import com.farouktouil.farouktouil.ui.theme.onPrimaryLight
import com.farouktouil.farouktouil.ui.theme.primaryLight
@Composable
fun OrderUiListItem(
    orderListItem: OrderListItem,
    onDeleteClick: () -> Unit, // Delete function
    modifier: Modifier = Modifier
) {
    var isExportVisible by remember { mutableStateOf(false) } // State to toggle ExportScreen visibility

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    orderListItem.delivererName,
                    fontWeight = FontWeight.Bold,
                    color = primaryLight,
                    fontSize = 20.sp
                )
                Text(
                    "%.2f".format(orderListItem.totalAmount) + " DZ",
                    fontWeight = FontWeight.Bold,
                    color = primaryLight,
                    fontSize = 20.sp
                )
            }
            Row {
                // 📂 Toggle Button for ExportScreen
                IconButton(onClick = { isExportVisible = !isExportVisible }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert, // Three-dot menu icon
                        contentDescription = "More Options",
                        tint = Color.Gray
                    )
                }

                // 🗑️ Delete Icon aligned to the right
                IconButton(onClick = onDeleteClick) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Order",
                        tint = Color.Red
                    )
                }
            }
        }
        Divider(color = Color.Gray)
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                orderListItem.orderDate,
                color = primaryLight,
                fontSize = 16.sp
            )
        }

        // 🎬 Animated Visibility for ExportScreen
        AnimatedVisibility(visible = isExportVisible) {
            ExportScreen()
        }
    }
}
