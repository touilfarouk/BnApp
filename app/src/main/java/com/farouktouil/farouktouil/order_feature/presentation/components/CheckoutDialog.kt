package com.farouktouil.farouktouil.order_feature.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.farouktouil.farouktouil.order_feature.presentation.state.ProductListItem


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CheckoutDialog(
    onDismiss:()->Unit,
    onConfirm:()->Unit, 
    
    onExport:()->Unit,
    selectedProducts:List<ProductListItem>,
    isExporting:Boolean,
    exportProgress:Int,
) {

    val cardGradient = Brush.linearGradient(
        colors = listOf(
            Color(0xFFB9FF6C),
            Color(0xFFE5FF8C),
            Color(0xFFA6FFAF)
        )
    )

    Dialog(
        onDismissRequest = {
            onDismiss()
        },
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ){
        Card(
            shape = RoundedCornerShape(15.dp),
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.8f)
                .border(1.dp, color = Color.Gray, shape = RoundedCornerShape(15.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(15.dp)
            ) {
                Text(
                    "Checkout",
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth()
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                if (selectedProducts.isNotEmpty()) {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(15.dp),
                        contentPadding = PaddingValues(top = 15.dp)
                    ) {
                        items(
                            selectedProducts,
                            key = { productListItem ->
                                productListItem.id
                            }
                        ) { item ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        brush = cardGradient,
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .border(
                                        width = 1.dp,
                                        color = Color.LightGray,
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = "${item.selectedAmount}x ${item.name}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                item.structureName?.takeIf { name -> name.isNotBlank() }?.let { structure ->
                                    Text(
                                        text = "Structure : $structure",
                                        fontSize = 14.sp,
                                        color = Color.DarkGray
                                    )
                                }
                                item.assignedPersonnelName?.takeIf { name -> name.isNotBlank() }?.let { personnel ->
                                    Text(
                                        text = "Personnel : $personnel",
                                        fontSize = 14.sp,
                                        color = Color.DarkGray
                                    )
                                }
                                if (item.accessories.isNotEmpty()) {
                                    val accessoriesSummary = item.accessories.joinToString(
                                        separator = ", "
                                    ) { accessory ->
                                        accessory.name.lowercase().replaceFirstChar { char ->
                                            char.titlecase()
                                        }
                                    }
                                    Text(
                                        text = "Accessoires : $accessoriesSummary",
                                        fontSize = 13.sp,
                                        color = Color.Gray
                                    )
                                }
                                if (item.label.isNotBlank()) {
                                    Text(
                                        text = item.label,
                                        fontSize = 13.sp,
                                        color = Color.Gray
                                    )
                                }
                            }
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Please select items to order")
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilledIconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(48.dp),
                        shape = CircleShape,
                        colors = IconButtonDefaults.filledIconButtonColors(
                            contentColor = Color.Gray
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Fermer"
                        )
                    }
                    Button(
                        onClick = onExport,
                        enabled = !isExporting,
                        colors = ButtonDefaults.buttonColors(
                            contentColor = Color.Gray
                        ),
                        modifier = Modifier.weight(1f),
                        shape = CircleShape
                    ) {
                        if (isExporting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Export... $exportProgress%",
                                fontWeight = FontWeight.Bold
                            )
                        } else {
                            Text(
                                text = "Exporter",
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                    Button(
                        onClick = onConfirm,
                        colors = ButtonDefaults.buttonColors(
                            contentColor = Color.Gray
                        ),
                        modifier = Modifier.weight(1f),
                        shape = CircleShape
                    ) {
                        Text(
                            text = "Confirmer",
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}
