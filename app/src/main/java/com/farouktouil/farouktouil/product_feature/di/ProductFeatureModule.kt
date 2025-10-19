package com.farouktouil.farouktouil.product_feature.di

import com.farouktouil.farouktouil.product_feature.data.repository.ProductRepositoryImpl
import com.farouktouil.farouktouil.product_feature.domain.repository.ProductsRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object ProductModule {
    @Provides
    @Singleton
    fun provideProductRepository(
        productRepositoryImpl: ProductRepositoryImpl
    ): ProductsRepository {
        return productRepositoryImpl
    }
}
