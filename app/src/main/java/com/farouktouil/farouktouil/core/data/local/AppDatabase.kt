package com.farouktouil.farouktouil.core.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.farouktouil.farouktouil.consultation_feature.data.local.dao.AppelConsultationDao
import com.farouktouil.farouktouil.consultation_feature.data.local.entity.AppelConsultationEntity

import com.farouktouil.farouktouil.core.data.local.entities.DelivererEntity
import com.farouktouil.farouktouil.core.data.local.entities.OrderEntity
import com.farouktouil.farouktouil.core.data.local.entities.OrderProductEntity
import com.farouktouil.farouktouil.core.data.local.entities.ProductEntity
import com.farouktouil.farouktouil.personnel_feature.data.local.PersonnelEntity
import com.farouktouil.farouktouil.personnel_feature.data.local.dao.PersonnelDao
import com.farouktouil.farouktouil.personnel_feature.data.local.dao.RemoteKeysDao
import com.farouktouil.farouktouil.personnel_feature.data.local.entities.RemoteKey

@Database(
    entities = [
        DelivererEntity::class,
        OrderEntity::class,
        OrderProductEntity::class,
        ProductEntity::class,
        PersonnelEntity::class,
        RemoteKey::class,
        AppelConsultationEntity::class
    ],
    version = 30, 
    exportSchema = false
)
abstract class AppDatabase:RoomDatabase() {
    abstract fun orderDao():OrderDao
    abstract fun productDao():ProductDao
    abstract fun delivererDao():DelivererDao
    abstract fun personnelDao(): PersonnelDao
    abstract fun remoteKeysDao(): RemoteKeysDao
    abstract fun appelConsultationDao(): AppelConsultationDao
}