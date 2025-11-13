package com.farouktouil.farouktouil.order_feature.presentation.state

/**
 * Holds UI state for exporting the current affectation selection from the checkout flow.
 */
data class AffectationExportState(
    val isExporting: Boolean = false,
    val progressPercentage: Int = 0,
    val exportFilePath: String? = null,
    val isShareRequested: Boolean = false,
    val errorMessage: String? = null
)
