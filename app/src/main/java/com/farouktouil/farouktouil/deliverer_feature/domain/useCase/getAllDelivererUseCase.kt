package com.farouktouil.farouktouil.deliverer_feature.domain.useCase


import com.farouktouil.farouktouil.deliverer_feature.domain.repository.DelivererRepository
import javax.inject.Inject

class GetAllDeliverersUseCase @Inject constructor(private val delivererRepository: DelivererRepository) {
    operator fun invoke() = delivererRepository.getAllDeliverers()

}
