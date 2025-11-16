package com.farouktouil.farouktouil.product_feature.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface InventoryApiService {
    @POST("remote-api-server.php")
    suspend fun createProduct(
        @Body request: InventoryCreateProductRequest
    ): Response<InventoryApiResponse>

    @POST("remote-api-server.php")
    suspend fun listStructures(
        @Body request: InventoryActionRequest = InventoryActionRequest(action = "LIST_STRUCTURES")
    ): Response<InventoryStructuresResponse>

    @POST("remote-api-server.php")
    suspend fun listPersonnel(
        @Body request: InventoryPersonnelRequest
    ): Response<InventoryPersonnelResponse>
}
