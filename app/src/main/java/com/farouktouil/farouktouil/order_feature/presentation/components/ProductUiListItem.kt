package com.farouktouil.farouktouil.order_feature.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.farouktouil.farouktouil.order_feature.presentation.state.ProductListItem


@Composable
fun ProductUiListItem(
    productListItem: ProductListItem,
    isExpanded:Boolean,
    onPlusClick:()->Unit,
    onMinusClick:()->Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ){
            Column(
                verticalArrangement = Arrangement.spacedBy(5.dp),
                horizontalAlignment = Alignment.Start
            ){
                Text(
                    productListItem.name,
                    color =  Color.Gray,
                    fontWeight = FontWeight.Bold
                )
                if (productListItem.label.isNotEmpty()) {
                    Text(
                        productListItem.label,
                        color =  Color.Gray,
                        fontSize = 12.sp,
                        style = androidx.compose.material3.MaterialTheme.typography.bodySmall
                    )
                }
                Text(
                    "%.2f".format(productListItem.pricePerAmount)+" DZ",
                    color =  Color.Gray,
                    fontSize = 14.sp
                )
            }
            AnimatedVisibility(productListItem.selectedAmount>0) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "checkmark",
                        tint =  Color.Gray
                    )
                    Text(
                        "${productListItem.selectedAmount} x",
                        color =  Color.Gray
                    )
                }
            }
        }
        AnimatedVisibility(isExpanded) {
            Divider(color =  Color.Gray)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(25.dp)
            ){
                IconButton(onClick = {
                    onMinusClick()
                }){
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = "minus",
                        tint =  Color.Gray
                    )
                }
                IconButton(onClick = {
                    onPlusClick()
                }){
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "plus",
                        tint =  Color.Gray
                    )
                }
            }
        }
    }
}