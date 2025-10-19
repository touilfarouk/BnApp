package com.farouktouil.farouktouil.deliverer_feature.di


import com.farouktouil.farouktouil.deliverer_feature.data.repository.DelivererRepositoryImpl
import com.farouktouil.farouktouil.deliverer_feature.domain.repository.DelivererRepository
import com.farouktouil.farouktouil.core.data.local.DelivererDao
import com.farouktouil.farouktouil.core.data.local.ProductDao
import com.farouktouil.farouktouil.deliverer_feature.domain.useCase.DeleteDelivererUseCase
import com.farouktouil.farouktouil.deliverer_feature.domain.useCase.GetAllDeliverersUseCase
import com.farouktouil.farouktouil.deliverer_feature.domain.useCase.InsertDelivererUseCase
import com.farouktouil.farouktouil.deliverer_feature.domain.useCase.UpdateDelivererUseCase
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DelivererModule {
    @Provides
    @Singleton
    fun provideDelivererRepository(
        delivererRepositoryImpl: DelivererRepositoryImpl
    ): DelivererRepository {
        return delivererRepositoryImpl
    }
}
