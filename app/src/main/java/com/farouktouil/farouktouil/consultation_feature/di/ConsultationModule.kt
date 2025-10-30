package com.farouktouil.farouktouil.consultation_feature.di

import com.farouktouil.farouktouil.consultation_feature.data.remote.ConsultationApiService
import com.farouktouil.farouktouil.consultation_feature.data.repository.ConsultationRepositoryImpl
import com.farouktouil.farouktouil.consultation_feature.domain.repository.ConsultationRepository
import com.farouktouil.farouktouil.consultation_feature.domain.use_case.GetConsultationCallsUseCase
import com.farouktouil.farouktouil.core.data.local.AppDatabase
import com.farouktouil.farouktouil.core.util.NetworkUtils
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ConsultationModule {

    @Provides
    @Singleton
    fun provideConsultationRepository(
        db: AppDatabase,
        api: ConsultationApiService,
        networkUtils: NetworkUtils
    ): ConsultationRepository {
        return ConsultationRepositoryImpl(db, api, networkUtils)
    }

    @Provides
    @Singleton
    fun provideGetConsultationCallsUseCase(
        consultationRepository: ConsultationRepository
    ): GetConsultationCallsUseCase {
        return GetConsultationCallsUseCase(consultationRepository)
    }
}
