package com.farouktouil.farouktouil.core.di

import android.content.Context
import androidx.room.Room
import com.farouktouil.farouktouil.core.data.local.AppDatabase
import com.farouktouil.farouktouil.core.data.local.DelivererDao
import com.farouktouil.farouktouil.core.data.local.OrderDao
import com.farouktouil.farouktouil.core.data.local.ProductDao

import com.farouktouil.farouktouil.personnel_feature.data.local.dao.PersonnelDao
import com.farouktouil.farouktouil.personnel_feature.data.local.dao.RemoteKeysDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "app_database"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun provideOrderDao(appDatabase: AppDatabase): OrderDao {
        return appDatabase.orderDao()
    }

    @Provides
    @Singleton
    fun provideProductDao(appDatabase: AppDatabase): ProductDao {
        return appDatabase.productDao()
    }

    @Provides
    @Singleton
    fun provideDelivererDao(appDatabase: AppDatabase): DelivererDao {
        return appDatabase.delivererDao()
    }

    @Provides
    @Singleton
    fun providePersonnelDao(appDatabase: AppDatabase): PersonnelDao {
        return appDatabase.personnelDao()
    }

    @Provides
    @Singleton
    fun provideRemoteKeysDao(appDatabase: AppDatabase): RemoteKeysDao {
        return appDatabase.remoteKeysDao()
    }
}
