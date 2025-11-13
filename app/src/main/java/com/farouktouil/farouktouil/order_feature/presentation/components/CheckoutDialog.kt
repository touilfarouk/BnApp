package com.farouktouil.farouktouil.order_feature.presentation.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
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

    Dialog(
        onDismissRequest = {
            onDismiss()
        },
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ){
        Card(
           // elevation = ,
            shape = RoundedCornerShape(15.dp),
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.8f)
                .border(1.dp, color = Color.Gray, shape = RoundedCornerShape(15.dp))
        ){
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(15.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                Column {
                    Text(
                        "Checkout",
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Divider(modifier = Modifier.padding(10.dp))
                    if(selectedProducts.isNotEmpty()){
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(15.dp),
                            modifier = Modifier
                                .padding(top = 15.dp)
                        ){
                            items(
                                selectedProducts,
                                key = {productListItem ->
                                    productListItem.id
                                }
                            ){
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .border(
                                            width = 1.dp,
                                            color = Color.LightGray,
                                            shape = RoundedCornerShape(10.dp)
                                        )
                                        .padding(12.dp)
                                ) {
                                    Text(
                                        text = "${it.selectedAmount}x ${it.name}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                    it.structureName?.takeIf { name -> name.isNotBlank() }?.let { structure ->
                                        Text(
                                            text = "Structure : $structure",
                                            fontSize = 14.sp,
                                            color = Color.DarkGray
                                        )
                                    }
                                    it.assignedPersonnelName?.takeIf { name -> name.isNotBlank() }?.let { personnel ->
                                        Text(
                                            text = "Personnel : $personnel",
                                            fontSize = 14.sp,
                                            color = Color.DarkGray
                                        )
                                    }
                                    if (it.accessories.isNotEmpty()) {
                                        val accessoriesSummary = it.accessories.joinToString(
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
                                    if (it.label.isNotBlank()) {
                                        Text(
                                            text = it.label,
                                            fontSize = 13.sp,
                                            color = Color.Gray
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Total : %.2f DZ".format(it.pricePerAmount * it.selectedAmount),
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 14.sp,
                                        textAlign = TextAlign.End,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }
                    }else{
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ){
                            Text("Please select items to order")
                        }
                    }
                }
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ){
                    Divider()
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ){
                        Text(
                            "Total",
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "%.2f".format(selectedProducts.sumOf { (it.selectedAmount*it.pricePerAmount).toDouble() })+" DZ",
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        Button(
                            onClick = onDismiss,
                            colors = ButtonDefaults.buttonColors(
                                contentColor = Color.Gray
                            ),
                            modifier = Modifier
                                .weight(1f),
                            shape = CircleShape
                        ){
                            Text(
                                text = "Fermer",
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                        }
                        Button(
                            onClick = onExport,
                            enabled = !isExporting,
                            colors = ButtonDefaults.buttonColors(
                                contentColor = Color.Gray
                            ),
                            modifier = Modifier
                                .weight(1f),
                            shape = CircleShape
                        ){
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
                            modifier = Modifier
                                .weight(1f),
                            shape = CircleShape
                        ){
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

}