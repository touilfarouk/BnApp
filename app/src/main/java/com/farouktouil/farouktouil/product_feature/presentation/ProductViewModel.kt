package com.farouktouil.farouktouil.product_feature.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.farouktouil.farouktouil.core.data.local.DelivererDao
import com.farouktouil.farouktouil.core.data.local.ProductDao
import com.farouktouil.farouktouil.core.data.local.entities.DelivererEntity
import com.farouktouil.farouktouil.core.data.local.entities.ProductEntity
import com.farouktouil.farouktouil.core.domain.model.Product
import com.farouktouil.farouktouil.product_feature.domain.useCase.DeleteProductUseCase
import com.farouktouil.farouktouil.product_feature.domain.useCase.GetAllProductsUseCase
import com.farouktouil.farouktouil.product_feature.domain.useCase.GetProductsForDelivererUseCase
import com.farouktouil.farouktouil.product_feature.domain.useCase.InsertProductUseCase
import com.farouktouil.farouktouil.product_feature.domain.useCase.UpdateProductUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val insertProductUseCase: InsertProductUseCase,
    private val deleteProductUseCase: DeleteProductUseCase,
    private val updateProductUseCase: UpdateProductUseCase,
    private val getAllProductsUseCase: GetAllProductsUseCase,
    private val getProductsForDelivererUseCase: GetProductsForDelivererUseCase, // UseCase to fetch products for a specific deliverer
    private val delivererDao: DelivererDao,
    private val productDao: ProductDao
) : ViewModel() {

    private val _deliverers = MutableStateFlow<List<DelivererEntity>>(emptyList())
    val deliverers: StateFlow<List<DelivererEntity>> = _deliverers.asStateFlow()

    private val _selectedDelivererId = MutableStateFlow<Int?>(null)
    val selectedDelivererId: StateFlow<Int?> = _selectedDelivererId.asStateFlow()

    private val _lowStockProducts = MutableStateFlow<List<Product>>(emptyList())
    val lowStockProducts: StateFlow<List<Product>> = _lowStockProducts.asStateFlow()

    private val _totalInventoryQuantity = MutableStateFlow(0)
    val totalInventoryQuantity: StateFlow<Int> = _totalInventoryQuantity.asStateFlow()

    private val _totalInventoryValue = MutableStateFlow(0f)
    val totalInventoryValue: StateFlow<Float> = _totalInventoryValue.asStateFlow()

    private val _lowStockCount = MutableStateFlow(0)
    val lowStockCount: StateFlow<Int> = _lowStockCount.asStateFlow()

    val uiState: StateFlow<ProductUiState> = combine(
        _selectedDelivererId.flatMapLatest { delivererId ->
            if (delivererId == null) {
                getAllProductsUseCase.invoke()
            } else {
                getProductsForDelivererUseCase.invoke(delivererId)
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
        fetchDeliverers()
        observeInventoryStats()
    }

    private fun fetchDeliverers() {
        viewModelScope.launch {
            delivererDao.getAllDeliverers()
                .collect { deliverers ->
                    _deliverers.value = deliverers
                }
        }
    }

    fun adjustProductQuantity(productId: Int, quantityChange: Int) = viewModelScope.launch {
        productDao.adjustProductQuantity(productId, quantityChange)
    }

    fun selectDeliverer(delivererId: Int?) {
        _selectedDelivererId.value = delivererId
    }

    fun update(product: Product) = viewModelScope.launch {
        updateProductUseCase.invoke(product)
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
        belongsToDeliverer: Int,
        barcode: String = ""
    ) = viewModelScope.launch {
        val product = Product(
            name = name,
            label = label,
            pricePerAmount = pricePerAmount.toFloat(),
            quantity = quantity,
            minQuantity = minQuantity,
            maxQuantity = maxQuantity,
            belongsToDeliverer = belongsToDeliverer,
            barcode = barcode
        )
        insertProductUseCase.invoke(product)
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
            belongsToDeliverer = this.belongsToDeliverer,
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
