package com.farouktouil.farouktouil.personnel_feature.data.remote

data class PersonnelDto(
    val id: Int,
    val username: String?,
    val password: String?,
    val name: String?,
    val nom: String?,
    val prenom: String?,
    val fonction: String?,
    val structure: String?,
    val maildir: String?,
    val quota: String?,
    val localPart: String?,
    val domain: String?,
    val matricule: Int?,
    val active: Int?,
    val mdpChanged: Int?,
    val dateRegister: String?,
    val modified: String?
)

data class PaginatedResponse<T>(
    val data: List<T>,
    val pagination: PaginationDto
)

data class PaginationDto(
    val currentPage: Int,
    val pageSize: Int,
    val totalItems: Int,
    val totalPages: Int,
    val hasNext: Boolean,
    val hasPrevious: Boolean
)
