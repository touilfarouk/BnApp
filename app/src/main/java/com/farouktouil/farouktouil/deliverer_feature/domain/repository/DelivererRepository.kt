package com.farouktouil.farouktouil.deliverer_feature.domain.repository


import com.farouktouil.farouktouil.core.domain.model.Deliverer
import com.farouktouil.farouktouil.core.domain.model.Product
import kotlinx.coroutines.flow.Flow


interface DelivererRepository {

    suspend fun insertDeliverers(list:List<Deliverer>)

    suspend fun insertProducts(list:List<Product>, delivererId:Int)

    suspend fun insert(deliverer: Deliverer)

    suspend fun update(deliverer: Deliverer)

    suspend fun delete(deliverer: Deliverer)

    fun getAllDeliverers(): Flow<List<Deliverer>>

    suspend fun refreshDeliverers()

    // ✅ Added function to get Deliverer IDs
    fun getAllDelivererIds(): Flow<List<Int>>
}