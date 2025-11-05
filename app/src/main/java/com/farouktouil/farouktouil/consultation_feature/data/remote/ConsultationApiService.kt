package com.farouktouil.farouktouil.consultation_feature.data.remote

import com.farouktouil.farouktouil.consultation_feature.data.remote.dto.AppelConsultationDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

data class PaginatedResponse<T>(
    val data: List<T>,
    val pagination: Pagination
)

data class Pagination(
    val currentPage: Int,
    val pageSize: Int,
    val totalItems: Int,
    val totalPages: Int,
    val hasNext: Boolean,
    val hasPrevious: Boolean
)

interface ConsultationApiService {
    @GET("tenders/tenders-paginated.php")
    suspend fun getConsultationCalls(
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 10,
        @Query("search") search: String? = null
    ): Response<PaginatedResponse<AppelConsultationDto>>
}
