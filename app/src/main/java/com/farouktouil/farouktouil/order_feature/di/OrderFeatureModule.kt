package com.farouktouil.farouktouil.order_feature.di

import com.farouktouil.farouktouil.core.data.local.OrderDao
import com.farouktouil.farouktouil.core.data.local.ProductAccessoryDao
import com.farouktouil.farouktouil.core.data.local.ProductDao
import com.farouktouil.farouktouil.order_feature.data.repository.OrderRepositoryImpl
import com.farouktouil.farouktouil.order_feature.domain.repository.OrderRepository
import com.farouktouil.farouktouil.order_feature.domain.use_case.ConfirmOrderUseCase
import com.farouktouil.farouktouil.order_feature.domain.use_case.FilterListByNameUseCase
import com.farouktouil.farouktouil.order_feature.domain.use_case.SortListByNameUseCase
import com.farouktouil.farouktouil.personnel_feature.data.local.dao.PersonnelDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object OrderFeatureModule {

    @Provides
    @Singleton
    fun provideOrderRepository(
        orderDao:OrderDao,
        productDao: ProductDao,
        productAccessoryDao: ProductAccessoryDao,
        personnelDao: PersonnelDao
    ):OrderRepository{
        return OrderRepositoryImpl(orderDao,productDao,productAccessoryDao,personnelDao)
    }

    @Provides
    @Singleton
    fun provideFilterListByNameUseCase():FilterListByNameUseCase{
        return FilterListByNameUseCase()
    }

    @Provides
    @Singleton
    fun provideSortListByNameUseCase():SortListByNameUseCase{
        return SortListByNameUseCase()
    }

    @Provides
    @Singleton
    fun provideConfirmOrderUseCase(
        orderRepository: OrderRepository,
        productDao: ProductDao
    ): ConfirmOrderUseCase {
        return ConfirmOrderUseCase(orderRepository, productDao)
    }


}