package com.farouktouil.farouktouil.order_feature.di

import com.farouktouil.farouktouil.core.data.local.DelivererDao
import com.farouktouil.farouktouil.core.data.local.OrderDao
import com.farouktouil.farouktouil.core.data.local.ProductDao
import com.farouktouil.farouktouil.order_feature.data.repository.OrderRepositoryImpl
import com.farouktouil.farouktouil.order_feature.domain.repository.OrderRepository
import com.farouktouil.farouktouil.order_feature.domain.use_case.ConfirmOrderUseCase
import com.farouktouil.farouktouil.order_feature.domain.use_case.FilterListByNameUseCase
import com.farouktouil.farouktouil.order_feature.domain.use_case.SortListByNameUseCase
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
        delivererDao:DelivererDao,
        productDao: ProductDao
    ):OrderRepository{
        return OrderRepositoryImpl(orderDao,delivererDao,productDao)
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