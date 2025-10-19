package com.farouktouil.farouktouil.core.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.farouktouil.farouktouil.core.data.local.entities.DelivererEntity
import com.farouktouil.farouktouil.core.data.local.entities.DelivererWithProductsDataObject
import kotlinx.coroutines.flow.Flow

@Dao
interface DelivererDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeliverer(deliverer: DelivererEntity)

    @Update
    suspend fun updateDeliverer(deliverer: DelivererEntity)

    @Delete
    suspend fun deleteDeliverer(deliverer: DelivererEntity)

    @Query("SELECT * FROM DelivererEntity")
    fun getAllDeliverers(): Flow<List<DelivererEntity>>



    @Query("SELECT delivererId FROM DelivererEntity") // Fetch deliverer IDs
    fun getAllDelivererIds(): Flow<List<Int>>

    @Transaction
    @Query("SELECT * FROM DelivererEntity")
    fun getDeliverers(): Flow<List<DelivererWithProductsDataObject>>


    @Query("SELECT name FROM DELIVERERENTITY WHERE delivererId = :delivererId")
    suspend fun getDelivererNameById(delivererId:Int):String
}