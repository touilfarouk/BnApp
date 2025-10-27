package com.farouktouil.farouktouil.consultation_feature.di

import com.farouktouil.farouktouil.consultation_feature.data.remote.ConsultationApiService
import com.farouktouil.farouktouil.consultation_feature.data.remote.ConsultationRemoteDataSource
import com.farouktouil.farouktouil.consultation_feature.data.repository.ConsultationRepositoryImpl
import com.farouktouil.farouktouil.consultation_feature.domain.repository.ConsultationRepository
import com.farouktouil.farouktouil.consultation_feature.domain.use_case.GetConsultationCallsUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object ConsultationModule {

    @Provides
    fun provideConsultationRemoteDataSource(apiService: ConsultationApiService): ConsultationRemoteDataSource {
        return ConsultationRemoteDataSource(apiService)
    }

    @Provides
    fun provideConsultationRepository(
        consultationRemoteDataSource: ConsultationRemoteDataSource
    ): ConsultationRepository {
        return ConsultationRepositoryImpl(consultationRemoteDataSource)
    }

    @Provides
    fun provideGetConsultationCallsUseCase(
        consultationRepository: ConsultationRepository
    ): GetConsultationCallsUseCase {
        return GetConsultationCallsUseCase(consultationRepository)
    }
}
