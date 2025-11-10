package com.farouktouil.farouktouil.personnel_feature.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface PersonnelApiService {
    @GET("personnel-paginated.php")
    suspend fun getPersonnel(
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int,
        @Query("search") search: String? = null
    ): PaginatedResponse<PersonnelDto>
}
