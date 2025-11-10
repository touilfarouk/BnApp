package com.farouktouil.farouktouil.core.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.farouktouil.farouktouil.core.data.local.converters.Converters
import com.farouktouil.farouktouil.consultation_feature.data.local.dao.AppelConsultationDao
import com.farouktouil.farouktouil.consultation_feature.data.local.dao.DocumentDao
import com.farouktouil.farouktouil.consultation_feature.data.local.dao.RemoteKeysDao as ConsultationRemoteKeysDao
import com.farouktouil.farouktouil.consultation_feature.data.local.entity.AppelConsultationEntity
import com.farouktouil.farouktouil.consultation_feature.data.local.entity.DocumentEntity
import com.farouktouil.farouktouil.consultation_feature.data.local.entity.RemoteKey as ConsultationRemoteKey
import com.farouktouil.farouktouil.news_feature.data.local.dao.NewsDao
import com.farouktouil.farouktouil.news_feature.data.local.dao.NewsRemoteKeysDao
import com.farouktouil.farouktouil.news_feature.data.local.entity.NewsEntity
import com.farouktouil.farouktouil.news_feature.data.local.entity.NewsRemoteKey

import com.farouktouil.farouktouil.core.data.local.entities.DelivererEntity
import com.farouktouil.farouktouil.core.data.local.entities.OrderEntity
import com.farouktouil.farouktouil.core.data.local.entities.OrderProductEntity
import com.farouktouil.farouktouil.core.data.local.entities.ProductEntity
import com.farouktouil.farouktouil.personnel_feature.data.local.PersonnelEntity
import com.farouktouil.farouktouil.personnel_feature.data.local.dao.PersonnelDao
import com.farouktouil.farouktouil.core.data.local.migrations.MIGRATION_33_34
import com.farouktouil.farouktouil.core.data.local.migrations.MIGRATION_34_35
import com.farouktouil.farouktouil.core.data.local.migrations.MIGRATION_38_39
import com.farouktouil.farouktouil.core.data.local.migrations.MIGRATION_39_40
import com.farouktouil.farouktouil.core.data.local.migrations.MIGRATION_40_41
import com.farouktouil.farouktouil.core.data.local.migrations.MIGRATION_41_42
import com.farouktouil.farouktouil.core.data.local.migrations.MIGRATION_43_44
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
        ConsultationRemoteKey::class,
        DocumentEntity::class,
        NewsEntity::class,
        NewsRemoteKey::class
    ],
    version = 44, // Incremented version due to news feature pagination schema updates
    exportSchema = true
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
                .addMigrations(
                    MIGRATION_33_34,
                    MIGRATION_34_35,
                    MIGRATION_38_39,
                    MIGRATION_39_40,
                    MIGRATION_40_41,
                    MIGRATION_41_42,
                    MIGRATION_43_44
                )
                .fallbackToDestructiveMigration()
                .createFromAsset("database/initial_data.db") // Optional: If you have initial data
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
    abstract fun documentDao(): DocumentDao
    abstract fun newsDao(): NewsDao
    abstract fun newsRemoteKeysDao(): NewsRemoteKeysDao
}