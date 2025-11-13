package com.farouktouil.farouktouil

import android.app.Application
import com.farouktouil.farouktouil.order_feature.data.DummyDataSeeder
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class OrderApp : Application() {

    @Inject
    lateinit var dummyDataSeeder: DummyDataSeeder

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()
        applicationScope.launch {
            dummyDataSeeder.seedIfEmpty()
        }
    }
}