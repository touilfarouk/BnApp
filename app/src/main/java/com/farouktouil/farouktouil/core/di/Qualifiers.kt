package com.farouktouil.farouktouil.core.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DelivererApi

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class PersonnelApi

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ConsultationApi
