package com.farouktouil.farouktouil.contact_feature.domain.use_case

import com.farouktouil.farouktouil.contact_feature.domain.model.ContactMessage
import com.farouktouil.farouktouil.contact_feature.domain.model.Personnel
import com.farouktouil.farouktouil.contact_feature.domain.repository.ContactRepository

class SubmitContactMessageUseCase(
    private val contactRepository: ContactRepository
) {
    suspend operator fun invoke(contactMessage: ContactMessage): Result<Unit> {
        // Validate input
        if (contactMessage.name.isBlank()) {
            return Result.failure(IllegalArgumentException("Name is required"))
        }
        if (contactMessage.email.isBlank()) {
            return Result.failure(IllegalArgumentException("Email is required"))
        }
        if (contactMessage.message.isBlank()) {
            return Result.failure(IllegalArgumentException("Message is required"))
        }

        return contactRepository.submitContactMessage(contactMessage)
    }
}

class GetContactInfoUseCase(
    private val contactRepository: ContactRepository
) {
    suspend operator fun invoke() = contactRepository.getContactInfo()
}

class GetPersonnelUseCase(
    private val contactRepository: ContactRepository
) {
    suspend operator fun invoke(): Result<List<Personnel>> {
        return contactRepository.getPersonnel()
    }
}
