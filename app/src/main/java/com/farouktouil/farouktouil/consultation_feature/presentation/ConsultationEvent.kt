package com.farouktouil.farouktouil.consultation_feature.presentation

sealed class ConsultationEvent {
    data class OnNomAppelConsultationChanged(val value: String) : ConsultationEvent()
    data class OnDateDepotChanged(val value: String) : ConsultationEvent()
}
