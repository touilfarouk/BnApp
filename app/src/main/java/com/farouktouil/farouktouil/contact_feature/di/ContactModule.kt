package com.farouktouil.farouktouil.contact_feature.di

import com.farouktouil.farouktouil.contact_feature.data.repository.ContactRepositoryImpl
import com.farouktouil.farouktouil.contact_feature.data.remote.PersonnelRemoteDataSource
import com.farouktouil.farouktouil.contact_feature.domain.repository.ContactRepository
import com.farouktouil.farouktouil.contact_feature.domain.use_case.GetContactInfoUseCase
import com.farouktouil.farouktouil.contact_feature.domain.use_case.GetPersonnelUseCase
import com.farouktouil.farouktouil.contact_feature.domain.use_case.SubmitContactMessageUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object ContactModule {

    @Provides
    fun provideContactRepository(
        personnelRemoteDataSource: PersonnelRemoteDataSource
    ): ContactRepository {
        return ContactRepositoryImpl(personnelRemoteDataSource)
    }

    @Provides
    fun provideSubmitContactMessageUseCase(
        contactRepository: ContactRepository
    ): SubmitContactMessageUseCase {
        return SubmitContactMessageUseCase(contactRepository)
    }

    @Provides
    fun provideGetContactInfoUseCase(
        contactRepository: ContactRepository
    ): GetContactInfoUseCase {
        return GetContactInfoUseCase(contactRepository)
    }

    @Provides
    fun provideGetPersonnelUseCase(
        contactRepository: ContactRepository
    ): GetPersonnelUseCase {
        return GetPersonnelUseCase(contactRepository)
    }
}
