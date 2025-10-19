package com.farouktouil.farouktouil.core.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.farouktouil.farouktouil.core.data.local.entities.ProductEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(productEntity: ProductEntity)

    @Update
    suspend fun updateProduct(productEntity: ProductEntity)

    @Delete
    suspend fun deleteProduct(productEntity: ProductEntity)

    @Query("SELECT * FROM products")
    fun getAllProducts(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE belongsToDeliverer = :delivererId")
    fun getProductsForDeliverer(delivererId: Int): Flow<List<ProductEntity>>

    // Quantity-related operations
    @Query("SELECT * FROM products WHERE quantity <= minQuantity")
    fun getLowStockProducts(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE quantity > minQuantity AND quantity <= maxQuantity")
    fun getNormalStockProducts(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE quantity >= maxQuantity")
    fun getOverStockProducts(): Flow<List<ProductEntity>>

    @Query("UPDATE products SET quantity = :newQuantity WHERE productId = :productId")
    suspend fun updateProductQuantity(productId: Int, newQuantity: Int)

    @Query("UPDATE products SET quantity = quantity + :quantityChange WHERE productId = :productId")
    suspend fun adjustProductQuantity(productId: Int, quantityChange: Int)

    @Query("SELECT SUM(quantity) FROM products")
    fun getTotalInventoryQuantity(): Flow<Int?>

    @Query("DELETE FROM products")
    suspend fun deleteAllProducts()

    @Query("SELECT COUNT(*) FROM products WHERE quantity <= minQuantity")
    fun getLowStockCount(): Flow<Int>

    @Query("SELECT SUM(quantity * pricePerAmount) FROM products")
    fun getTotalInventoryValue(): Flow<Float?>

    @Query("SELECT * FROM products WHERE productId = :productId")
    suspend fun getProductById(productId: Int): ProductEntity?

    @Query("SELECT * FROM products WHERE barcode = :barcode")
    suspend fun getProductByBarcode(barcode: String): ProductEntity?

    @Query("SELECT * FROM products WHERE barcode = :barcode")
    fun getProductByBarcodeFlow(barcode: String): Flow<ProductEntity?>

    @Query("SELECT * FROM products WHERE name = :name LIMIT 1")
    suspend fun getProductByName(name: String): ProductEntity?

    @Query("SELECT * FROM products WHERE name = :name")
    fun getProductByNameFlow(name: String): Flow<ProductEntity?>
}
