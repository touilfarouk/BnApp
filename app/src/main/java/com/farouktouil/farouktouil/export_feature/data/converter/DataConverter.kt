package com.farouktouil.farouktouil.export_feature.data.converter

import com.farouktouil.farouktouil.core.util.Resource

import com.farouktouil.farouktouil.order_feature.domain.model.BoughtProduct
import com.farouktouil.farouktouil.order_feature.domain.model.Order
import kotlinx.coroutines.flow.Flow

interface DataConverter {

    fun convertSensorData(
        exportDataList:List<Order>
    ): Flow<Resource<GenerateInfo>>

}