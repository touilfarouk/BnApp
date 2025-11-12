package com.farouktouil.farouktouil.personnel_feature.di

import com.farouktouil.farouktouil.core.data.local.AppDatabase
import com.farouktouil.farouktouil.personnel_feature.data.remote.PersonnelApiService
import com.farouktouil.farouktouil.personnel_feature.data.remote.PersonnelRemoteDataSource
import com.farouktouil.farouktouil.personnel_feature.data.repository.PersonnelRepositoryImpl
import com.farouktouil.farouktouil.personnel_feature.domain.repository.PersonnelRepository
import com.farouktouil.farouktouil.personnel_feature.domain.use_case.GetPersonnelDirectoryUseCase
import com.farouktouil.farouktouil.personnel_feature.domain.use_case.GetPersonnelUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object PersonnelModule {

    @Provides
    fun providePersonnelRepository(
        database: AppDatabase,
        apiService: PersonnelApiService,
        remoteDataSource: PersonnelRemoteDataSource
    ): PersonnelRepository {
        return PersonnelRepositoryImpl(database, apiService, remoteDataSource)
    }

    @Provides
    fun provideGetPersonnelUseCase(
        personnelRepository: PersonnelRepository
    ): GetPersonnelUseCase {
        return GetPersonnelUseCase(personnelRepository)
    }

    @Provides
    fun provideGetPersonnelDirectoryUseCase(
        personnelRepository: PersonnelRepository
    ): GetPersonnelDirectoryUseCase {
        return GetPersonnelDirectoryUseCase(personnelRepository)
    }

    @Provides
    fun providePersonnelRemoteDataSource(
        apiService: PersonnelApiService
    ): PersonnelRemoteDataSource {
        return PersonnelRemoteDataSource(apiService)
    }
}
