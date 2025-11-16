package com.farouktouil.farouktouil.product_feature.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.farouktouil.farouktouil.core.data.local.ProductDao
import com.farouktouil.farouktouil.core.data.local.entities.ProductEntity
import com.farouktouil.farouktouil.core.domain.model.Product
import com.farouktouil.farouktouil.core.domain.model.AccessoryType
import com.farouktouil.farouktouil.product_feature.domain.useCase.DeleteProductUseCase
import com.farouktouil.farouktouil.product_feature.domain.useCase.GetAllProductsUseCase
import com.farouktouil.farouktouil.product_feature.domain.useCase.GetProductsForStructureUseCase
import com.farouktouil.farouktouil.product_feature.domain.useCase.InsertProductUseCase
import com.farouktouil.farouktouil.product_feature.domain.useCase.UpdateProductUseCase
import com.farouktouil.farouktouil.product_feature.domain.useCase.GetProductAccessoriesUseCase
import com.farouktouil.farouktouil.product_feature.presentation.state.PersonnelListItem
import com.farouktouil.farouktouil.personnel_feature.domain.use_case.GetPersonnelDirectoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val insertProductUseCase: InsertProductUseCase,
    private val deleteProductUseCase: DeleteProductUseCase,
    private val updateProductUseCase: UpdateProductUseCase,
    private val getAllProductsUseCase: GetAllProductsUseCase,
    private val getProductsForStructureUseCase: GetProductsForStructureUseCase,
    private val productDao: ProductDao,
    private val getPersonnelDirectoryUseCase: GetPersonnelDirectoryUseCase,
    private val getProductAccessoriesUseCase: GetProductAccessoriesUseCase
) : ViewModel() {

    private val _structures = MutableStateFlow<List<String>>(emptyList())
    val structures: StateFlow<List<String>> = _structures.asStateFlow()

    private val _selectedStructure = MutableStateFlow<String?>(null)
    val selectedStructure: StateFlow<String?> = _selectedStructure.asStateFlow()

    private val _personnel = MutableStateFlow<List<PersonnelListItem>>(emptyList())
    val personnel: StateFlow<List<PersonnelListItem>> = _personnel.asStateFlow()

    private val _isPersonnelLoading = MutableStateFlow(false)
    val isPersonnelLoading: StateFlow<Boolean> = _isPersonnelLoading.asStateFlow()

    private val _personnelError = MutableStateFlow<String?>(null)
    val personnelError: StateFlow<String?> = _personnelError.asStateFlow()

    private val _lowStockProducts = MutableStateFlow<List<Product>>(emptyList())
    val lowStockProducts: StateFlow<List<Product>> = _lowStockProducts.asStateFlow()

    private val _totalInventoryQuantity = MutableStateFlow(0)
    val totalInventoryQuantity: StateFlow<Int> = _totalInventoryQuantity.asStateFlow()

    private val _totalInventoryValue = MutableStateFlow(0f)
    val totalInventoryValue: StateFlow<Float> = _totalInventoryValue.asStateFlow()

    private val _lowStockCount = MutableStateFlow(0)
    val lowStockCount: StateFlow<Int> = _lowStockCount.asStateFlow()

    val uiState: StateFlow<ProductUiState> = combine(
        _selectedStructure.flatMapLatest { structureName ->
            if (structureName.isNullOrBlank()) {
                getAllProductsUseCase.invoke()
            } else {
                getProductsForStructureUseCase.invoke(structureName)
            }
        },
        lowStockProducts,
        totalInventoryQuantity,
        totalInventoryValue,
        lowStockCount
    ) { products, lowStock, totalQty, totalValue, lowStockCount ->
        ProductUiState(
            data = products,
            lowStockProducts = lowStock,
            totalInventoryQuantity = totalQty,
            totalInventoryValue = totalValue,
            lowStockCount = lowStockCount
        )
    }.stateIn(viewModelScope, SharingStarted.Eagerly, ProductUiState())

    init {
        refreshPersonnelDirectory()
        observeInventoryStats()
    }

    fun refreshPersonnelDirectory() {
        viewModelScope.launch {
            _isPersonnelLoading.value = true
            _personnelError.value = null

            runCatching { getPersonnelDirectoryUseCase() }
                .onSuccess { directory ->
                    val personnelItems = directory.map { personnel ->
                        PersonnelListItem(
                            id = personnel.id,
                            fullName = personnel.bestGuessName,
                            structureName = personnel.displayStructure
                        )
                    }.sortedBy { it.fullName.lowercase() }

                    _personnel.value = personnelItems

                    val structures = directory.mapNotNull { it.displayStructure }
                        .map { it.trim() }
                        .filter { it.isNotEmpty() }
                        .distinctBy { it.lowercase() }
                        .sortedBy { it.lowercase() }

                    _structures.value = structures

                    val currentSelection = _selectedStructure.value
                    if (currentSelection != null && currentSelection !in structures) {
                        _selectedStructure.value = null
                    }
                }
                .onFailure { throwable ->
                    _personnelError.value = throwable.message
                }

            _isPersonnelLoading.value = false
        }
    }

    fun adjustProductQuantity(productId: Int, quantityChange: Int) = viewModelScope.launch {
        productDao.adjustProductQuantity(productId, quantityChange)
    }

    fun selectStructure(structureName: String?) {
        _selectedStructure.value = structureName
    }

    fun update(product: Product, accessories: Set<AccessoryType>) = viewModelScope.launch {
        updateProductUseCase.invoke(product, accessories)
    }

    fun delete(product: Product) = viewModelScope.launch {
        deleteProductUseCase.invoke(product)
    }

    private fun observeInventoryStats() {
        viewModelScope.launch {
            productDao.getLowStockProducts().collect { products ->
                _lowStockProducts.value = products.map { it.toProduct() }
            }
        }

        viewModelScope.launch {
            productDao.getTotalInventoryQuantity().collect { quantity ->
                _totalInventoryQuantity.value = quantity ?: 0
            }
        }

        viewModelScope.launch {
            productDao.getTotalInventoryValue().collect { value ->
                _totalInventoryValue.value = value ?: 0f
            }
        }

        viewModelScope.launch {
            productDao.getLowStockCount().collect { count ->
                _lowStockCount.value = count
            }
        }
    }

    fun insert(
        name: String,
        label: String = "",
        pricePerAmount: Double,
        quantity: Int = 0,
        minQuantity: Int = 0,
        maxQuantity: Int = 100,
        structureName: String?,
        assignedPersonnelId: Int?,
        assignedPersonnelName: String?,
        accessories: Set<AccessoryType> = emptySet(),
        barcode: String = ""
    ) = viewModelScope.launch {
        val product = Product(
            name = name,
            label = label,
            pricePerAmount = pricePerAmount.toFloat(),
            quantity = quantity,
            minQuantity = minQuantity,
            maxQuantity = maxQuantity,
            structureName = structureName,
            assignedPersonnelId = assignedPersonnelId,
            assignedPersonnelName = assignedPersonnelName,
            barcode = barcode
        )
        insertProductUseCase.invoke(product, accessories)
    }

    suspend fun getAccessoriesForProduct(productId: Int): Set<AccessoryType> {
        if (productId <= 0) return emptySet()
        return getProductAccessoriesUseCase(productId)
    }

    suspend fun getProductById(productId: Int): ProductEntity? {
        return productDao.getProductById(productId)
    }

    suspend fun getProductByBarcode(barcode: String): ProductEntity? {
        return productDao.getProductByBarcode(barcode)
    }

    fun getProductByBarcodeFlow(barcode: String) = productDao.getProductByBarcodeFlow(barcode)

    suspend fun getProductByName(name: String): ProductEntity? {
        return productDao.getProductByName(name)
    }

    fun getProductByNameFlow(name: String) = productDao.getProductByNameFlow(name)

    fun updateProductQuantity(productId: Int, newQuantity: Int) = viewModelScope.launch {
        productDao.updateProductQuantity(productId, newQuantity)
    }

    private fun ProductEntity.toProduct(): Product {
        return Product(
            productId = this.productId,
            name = this.name,
            label = this.label,
            pricePerAmount = this.pricePerAmount,
            quantity = this.quantity,
            minQuantity = this.minQuantity,
            maxQuantity = this.maxQuantity,
            structureName = this.structureName,
            assignedPersonnelId = this.assignedPersonnelId,
            assignedPersonnelName = this.assignedPersonnelName,
            barcode = this.barcode
        )
    }

    data class ProductUiState(
        val data: List<Product> = emptyList(),
        val lowStockProducts: List<Product> = emptyList(),
        val totalInventoryQuantity: Int = 0,
        val totalInventoryValue: Float = 0f,
        val lowStockCount: Int = 0,
        val isLoading: Boolean = false
    )
}
