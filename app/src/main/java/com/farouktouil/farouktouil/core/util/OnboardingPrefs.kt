package com.farouktouil.farouktouil.core.util

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val ONBOARDING_PREFS_NAME = "onboarding_prefs"
private val Context.onboardingDataStore by preferencesDataStore(name = ONBOARDING_PREFS_NAME)

object OnboardingPrefs {
    private val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")

    fun isCompletedFlow(context: Context): Flow<Boolean> =
        context.onboardingDataStore.data.map { prefs ->
            prefs[ONBOARDING_COMPLETED] ?: false
        }

    suspend fun setCompleted(context: Context, completed: Boolean) {
        context.onboardingDataStore.edit { prefs ->
            prefs[ONBOARDING_COMPLETED] = completed
        }
    }
}
