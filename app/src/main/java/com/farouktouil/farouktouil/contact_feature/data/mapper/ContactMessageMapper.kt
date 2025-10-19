package com.farouktouil.farouktouil.contact_feature.data.mapper

import com.farouktouil.farouktouil.contact_feature.domain.model.ContactMessage

// Mappers for converting between different data representations
// Currently not needed for this simple contact feature, but included for completeness

object ContactMessageMapper {

    fun toDomain(contactMessage: ContactMessage): ContactMessage {
        return contactMessage
    }

    fun fromDomain(contactMessage: ContactMessage): ContactMessage {
        return contactMessage
    }
}
