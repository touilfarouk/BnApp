package com.farouktouil.farouktouil.core.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.farouktouil.farouktouil.core.data.local.entities.OrderEntity
import com.farouktouil.farouktouil.core.data.local.entities.OrderProductEntity
import com.farouktouil.farouktouil.core.data.local.entities.OrderWithProductsDataObject

@Dao

interface OrderDao {
    @Transaction
    @Query("SELECT * FROM OrderEntity")
    suspend fun getOrderWithProducts():List<OrderWithProductsDataObject>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(orderEntity: OrderEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrderProductEntities(orderProductEntities:List<OrderProductEntity>)

    @Transaction
    @Query("DELETE FROM OrderEntity WHERE orderId = :orderId")
    suspend fun deleteOrder(orderId: String)

    @Transaction
    @Query("DELETE FROM OrderProductEntity WHERE orderId = :orderId")
    suspend fun deleteOrderProducts(orderId: String)

}