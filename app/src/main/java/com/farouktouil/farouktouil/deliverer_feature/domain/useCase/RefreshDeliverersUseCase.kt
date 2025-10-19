package com.farouktouil.farouktouil.deliverer_feature.domain.useCase

import com.farouktouil.farouktouil.deliverer_feature.domain.repository.DelivererRepository
import javax.inject.Inject

class RefreshDeliverersUseCase @Inject constructor(
    private val delivererRepository: DelivererRepository
) {
    suspend operator fun invoke() {
        delivererRepository.refreshDeliverers()
    }
}