package com.farouktouil.farouktouil.product_feature.data.remote

data class InventoryCreateProductRequest(
    val action: String = "CREATE_PRODUCT",
    val form: InventoryProductForm
)

data class InventoryActionRequest(
    val action: String
)

data class InventoryStructuresResponse(
    val response: String?,
    val structures: List<InventoryStructureDto>?,
    val message: String?
)

data class InventoryStructureDto(
    val id: Int,
    val name: String,
    val address: String?
)

data class InventoryPersonnelRequest(
    val action: String = "LIST_PERSONNEL",
    val structure_id: Int? = null
)

data class InventoryPersonnelResponse(
    val response: String?,
    val personnel: List<InventoryPersonnelDto>?,
    val message: String?
)

data class InventoryPersonnelDto(
    val id: Int,
    val full_name: String,
    val structure_id: Int?
)

data class InventoryProductForm(
    val name: String,
    val label: String?,
    val structure_id: Int?,
    val structure_name: String?,
    val assigned_personnel_id: Int?,
    val assigned_personnel_name: String? = null,
    val accessories: List<String> = emptyList()
)

data class InventoryApiResponse(
    val response: String?,
    val message: String?,
    val id: Int?
) {
    val isSuccess: Boolean get() = response.equals("true", ignoreCase = true)
}
