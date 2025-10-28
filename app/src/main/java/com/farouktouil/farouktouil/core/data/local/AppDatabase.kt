package com.farouktouil.farouktouil.core.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.farouktouil.farouktouil.core.data.local.entities.DelivererEntity
import com.farouktouil.farouktouil.core.data.local.entities.OrderEntity
import com.farouktouil.farouktouil.core.data.local.entities.OrderProductEntity
import com.farouktouil.farouktouil.core.data.local.entities.ProductEntity

@Database(
    entities = [
        DelivererEntity::class,
        OrderEntity::class,
        OrderProductEntity::class,
        ProductEntity::class
    ],
    version = 28,
    exportSchema = false
)
abstract class AppDatabase:RoomDatabase() {
    abstract fun orderDao():OrderDao
    abstract fun productDao():ProductDao
    abstract fun delivererDao():DelivererDao
}