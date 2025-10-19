package com.farouktouil.farouktouil.deliverer_feature.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface DelivererApiService {
    @GET("index.php")
    suspend fun getDeliverers(
        @Query("resource") resource: String = "deliverers",
        @Query("name") name: String = "",
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20,
        @Query("key") key: String = "4c9f8d9a-1b2e-4f3a-ae0d-2b7f6c9d5e11"
    ): DelivererResponse
}