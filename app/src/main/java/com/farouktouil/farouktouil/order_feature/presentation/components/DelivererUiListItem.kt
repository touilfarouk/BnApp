package com.farouktouil.farouktouil.order_feature.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.farouktouil.farouktouil.order_feature.presentation.state.DelivererListItem
import com.farouktouil.farouktouil.ui.theme.onPrimaryLight
import com.farouktouil.farouktouil.ui.theme.primaryLight


@Composable
fun DelivererUiListItem(
    delivererListItem: DelivererListItem,
    modifier: Modifier = Modifier
) {

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ){
        Text(
            delivererListItem.name,
            color =  primaryLight
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End // Aligns content to the end (right)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowForwardIos,
                contentDescription = "arrow_right",
                tint = primaryLight
            )
        }
    }
}