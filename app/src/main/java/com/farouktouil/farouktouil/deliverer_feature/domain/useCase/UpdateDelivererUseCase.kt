package com.farouktouil.farouktouil.deliverer_feature.domain.useCase


import com.farouktouil.farouktouil.core.domain.model.Deliverer
import com.farouktouil.farouktouil.deliverer_feature.domain.repository.DelivererRepository
import javax.inject.Inject

class UpdateDelivererUseCase @Inject constructor(private val delivererRepository: DelivererRepository) {

    suspend operator fun invoke(deliverer: Deliverer) = delivererRepository.update(deliverer)

}
