package com.farouktouil.farouktouil.core.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.farouktouil.farouktouil.core.data.local.entities.ProductAccessoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductAccessoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(accessoryEntity: ProductAccessoryEntity)

    @Query("SELECT * FROM product_accessories")
    fun observeAll(): Flow<List<ProductAccessoryEntity>>

    @Query("SELECT * FROM product_accessories WHERE productId IN (:productIds)")
    suspend fun getAccessoriesForProducts(productIds: List<Int>): List<ProductAccessoryEntity>

    @Query("SELECT * FROM product_accessories WHERE productId = :productId")
    suspend fun getAccessoriesForProduct(productId: Int): ProductAccessoryEntity?

    @Query("DELETE FROM product_accessories WHERE productId = :productId")
    suspend fun deleteForProduct(productId: Int)
}
