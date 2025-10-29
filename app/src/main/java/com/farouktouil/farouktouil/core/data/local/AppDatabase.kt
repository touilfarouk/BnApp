package com.farouktouil.farouktouil.core.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.farouktouil.farouktouil.core.data.local.converters.Converters
import com.farouktouil.farouktouil.consultation_feature.data.local.dao.AppelConsultationDao
import com.farouktouil.farouktouil.consultation_feature.data.local.dao.RemoteKeysDao as ConsultationRemoteKeysDao
import com.farouktouil.farouktouil.consultation_feature.data.local.entity.AppelConsultationEntity
import com.farouktouil.farouktouil.consultation_feature.data.local.entity.RemoteKey as ConsultationRemoteKey

import com.farouktouil.farouktouil.core.data.local.entities.DelivererEntity
import com.farouktouil.farouktouil.core.data.local.entities.OrderEntity
import com.farouktouil.farouktouil.core.data.local.entities.OrderProductEntity
import com.farouktouil.farouktouil.core.data.local.entities.ProductEntity
import com.farouktouil.farouktouil.personnel_feature.data.local.PersonnelEntity
import com.farouktouil.farouktouil.personnel_feature.data.local.dao.PersonnelDao
import com.farouktouil.farouktouil.core.data.local.migrations.MIGRATION_33_34
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
        AppelConsultationEntity::class,
        ConsultationRemoteKey::class
    ],
    version = 35,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    companion object {
        const val DATABASE_NAME = "app_database"
        
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                )
                .fallbackToDestructiveMigration() // This will clear the database on version change
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
    abstract fun orderDao():OrderDao
    abstract fun productDao():ProductDao
    abstract fun delivererDao():DelivererDao
    abstract fun personnelDao(): PersonnelDao
    abstract fun remoteKeysDao(): RemoteKeysDao
    abstract fun consultationRemoteKeysDao(): ConsultationRemoteKeysDao
    abstract fun appelConsultationDao(): AppelConsultationDao
}