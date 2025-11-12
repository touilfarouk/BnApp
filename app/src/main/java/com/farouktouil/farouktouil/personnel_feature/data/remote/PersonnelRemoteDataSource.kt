package com.farouktouil.farouktouil.personnel_feature.data.remote

import javax.inject.Inject

class PersonnelRemoteDataSource @Inject constructor(
    private val personnelApiService: PersonnelApiService
) {

    suspend fun fetchAllPersonnel(
        pageSize: Int = DEFAULT_PAGE_SIZE,
        maxPages: Int = MAX_PAGE_LIMIT,
        search: String? = null
    ): List<PersonnelDto> {
        val collected = mutableListOf<PersonnelDto>()
        var currentPage = 1
        var hasNextPage: Boolean

        do {
            val response = personnelApiService.getPersonnel(
                page = currentPage,
                pageSize = pageSize,
                search = search
            )

            collected += response.data
            hasNextPage = response.pagination.hasNext && currentPage < maxPages
            currentPage += 1
        } while (hasNextPage)

        return collected
    }

    companion object {
        private const val DEFAULT_PAGE_SIZE = 100
        private const val MAX_PAGE_LIMIT = 100
    }
}
