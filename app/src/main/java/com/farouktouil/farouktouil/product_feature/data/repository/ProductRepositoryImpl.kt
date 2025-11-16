package com.farouktouil.farouktouil.product_feature.data.repository

import com.farouktouil.farouktouil.core.data.local.ProductAccessoryDao
import com.farouktouil.farouktouil.core.data.local.ProductDao
import com.farouktouil.farouktouil.core.domain.model.AccessoryType
import com.farouktouil.farouktouil.core.domain.model.Product
import android.util.Log
import com.farouktouil.farouktouil.product_feature.data.mapper.toAccessoryEntity
import com.farouktouil.farouktouil.product_feature.data.mapper.toAccessorySet
import com.farouktouil.farouktouil.product_feature.data.mapper.toProduct
import com.farouktouil.farouktouil.product_feature.data.mapper.toProductEntity
import com.farouktouil.farouktouil.product_feature.data.remote.InventoryApiService
import com.farouktouil.farouktouil.product_feature.data.remote.InventoryCreateProductRequest
import com.farouktouil.farouktouil.product_feature.data.remote.InventoryProductForm
import com.farouktouil.farouktouil.product_feature.data.remote.InventoryPersonnelRequest
import com.farouktouil.farouktouil.product_feature.domain.repository.ProductsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val productDao: ProductDao,
    private val productAccessoryDao: ProductAccessoryDao,
    private val inventoryApiService: InventoryApiService
) : ProductsRepository {

    override suspend fun insert(product: Product): Int {
        return productDao.insertProduct(product.toProductEntity()).toInt()
    }

    override suspend fun update(product: Product) {
        productDao.updateProduct(product.toProductEntity())
    }

    override suspend fun delete(product: Product) {
        productDao.deleteProduct(product.toProductEntity())
        if (product.productId != 0) {
            productAccessoryDao.deleteForProduct(product.productId)
        }
    }

    override fun getAllProducts(): Flow<List<Product>> {
        return productDao.getAllProducts()
            .map { list -> list.map { it.toProduct() } }
    }

    override fun getProductsForStructure(structureName: String): Flow<List<Product>> {
        return productDao.getProductsForStructure(structureName)
            .map { list -> list.map { it.toProduct() } }
    }

    override suspend fun upsertAccessories(productId: Int, accessories: Set<AccessoryType>) {
        if (productId <= 0) return
        if (accessories.isEmpty()) {
            productAccessoryDao.deleteForProduct(productId)
        } else {
            productAccessoryDao.upsert(accessories.toAccessoryEntity(productId))
        }
    }

    override suspend fun getAccessories(productId: Int): Set<AccessoryType> {
        if (productId <= 0) return emptySet()
        return productAccessoryDao.getAccessoriesForProduct(productId)?.toAccessorySet() ?: emptySet()
    }

    override suspend fun getProductById(productId: Int): Product? {
        if (productId <= 0) return null
        return productDao.getProductById(productId)?.toProduct()
    }

    override suspend fun pushProductToRemote(product: Product, accessories: Set<AccessoryType>) {
        runCatching {
            val productForSync = getProductById(product.productId) ?: product

            val accessoryLabels = accessories.map { it.name }
            val structureId = resolveStructureId(productForSync.structureName)
            val personnelId = resolvePersonnelId(
                productForSync.assignedPersonnelId,
                productForSync.assignedPersonnelName,
                structureId
            )

            Log.d(
                "ProductRepository",
                "Remote sync context -> structureName=${productForSync.structureName}, resolvedStructureId=$structureId, " +
                    "personnelName=${productForSync.assignedPersonnelName}, resolvedPersonnelId=$personnelId, accessories=$accessoryLabels"
            )

            val request = InventoryCreateProductRequest(
                form = InventoryProductForm(
                    name = productForSync.name,
                    label = productForSync.label.ifBlank { null },
                    structure_id = structureId,
                    structure_name = productForSync.structureName,
                    assigned_personnel_id = personnelId,
                    assigned_personnel_name = productForSync.assignedPersonnelName,
                    accessories = accessoryLabels
                )
            )

            Log.d("ProductRepository", "Sending remote product: $request")

            val response = inventoryApiService.createProduct(request)
            if (!response.isSuccessful) {
                throw IllegalStateException("Remote API error: ${response.code()} ${response.message()}")
            }

            val body = response.body()
            if (body?.isSuccess == false) {
                throw IllegalStateException(body.message ?: "Remote API indicated failure")
            }

            Log.d(
                "ProductRepository",
                "Remote product push succeeded: code=${response.code()} body=$body"
            )
        }.onFailure { throwable ->
            Log.w(
                "ProductRepository",
                "Failed to push product to remote. Product=${product.name}, requestAccessories=$accessories",
                throwable
            )
        }
    }

    private suspend fun resolveStructureId(structureName: String?): Int? {
        val name = structureName?.trim().orEmpty()
        if (name.isEmpty()) return null

        return structureIdCache[name.lowercase()] ?: run {
            val response = inventoryApiService.listStructures()
            if (!response.isSuccessful) {
                throw IllegalStateException("Failed to list structures: ${response.code()} ${response.message()}")
            }

            val body = response.body()
            val structures = body?.structures.orEmpty()
            structures.forEach { dto ->
                structureIdCache[dto.name.lowercase()] = dto.id
            }

            structureIdCache[name.lowercase()]
        }
    }

    private suspend fun resolvePersonnelId(
        assignedPersonnelId: Int?,
        assignedPersonnelName: String?,
        structureId: Int?
    ): Int? {
        val nameKey = assignedPersonnelName?.trim()?.lowercase().orEmpty()
        if (nameKey.isEmpty()) return null

        personnelIdCache[nameKey]?.let { return it }

        val response = inventoryApiService.listPersonnel(
            InventoryPersonnelRequest(structure_id = structureId)
        )
        if (!response.isSuccessful) {
            throw IllegalStateException("Failed to list personnel: ${response.code()} ${response.message()}")
        }

        val body = response.body()
        val remotePersonnel = body?.personnel.orEmpty()
        remotePersonnel.forEach { dto ->
            dto.full_name?.lowercase()?.let { key ->
                personnelIdCache[key] = dto.id
            }
        }

        return personnelIdCache[nameKey]
            ?: assignedPersonnelId // fallback to provided ID if remote lookup failed
    }

    private val structureIdCache = mutableMapOf<String, Int>()
    private val personnelIdCache = mutableMapOf<String, Int>()
}
