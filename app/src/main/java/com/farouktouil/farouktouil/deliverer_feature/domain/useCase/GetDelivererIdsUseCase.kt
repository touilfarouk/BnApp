package com.farouktouil.farouktouil.deliverer_feature.domain.useCase


import com.farouktouil.farouktouil.deliverer_feature.domain.repository.DelivererRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetDelivererIdsUseCase @Inject constructor(
    private val repository: DelivererRepository
) {
    operator fun invoke(): Flow<List<Int>> { // Return IDs instead of names
        return repository.getAllDelivererIds()
    }
}
