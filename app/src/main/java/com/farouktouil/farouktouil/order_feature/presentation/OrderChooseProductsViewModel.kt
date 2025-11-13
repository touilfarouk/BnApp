package com.farouktouil.farouktouil.order_feature.presentation

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.farouktouil.farouktouil.core.domain.model.AccessoryType
import com.farouktouil.farouktouil.core.domain.model.Product
import com.farouktouil.farouktouil.core.util.Resource
import com.farouktouil.farouktouil.export_feature.domain.repository.ExportRepository
import com.farouktouil.farouktouil.order_feature.domain.model.Order
import com.farouktouil.farouktouil.order_feature.domain.repository.OrderRepository
import com.farouktouil.farouktouil.order_feature.domain.use_case.ConfirmOrderUseCase
import com.farouktouil.farouktouil.order_feature.domain.use_case.SortListByNameUseCase
import com.farouktouil.farouktouil.order_feature.presentation.mapper.toBoughtProduct
import com.farouktouil.farouktouil.order_feature.presentation.mapper.toProductListItem
import com.farouktouil.farouktouil.order_feature.presentation.state.AffectationExportState
import com.farouktouil.farouktouil.order_feature.presentation.state.ProductListItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class OrderChooseProductsViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
    private val sortListByNameUseCase: SortListByNameUseCase,
    private val confirmOrderUseCase: ConfirmOrderUseCase,
    private val exportRepository: ExportRepository
) : ViewModel() {

    // StateFlow for the list of products
    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products

    // StateFlow for the search query
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    // StateFlow for the filtered and sorted list of products to display
    private val _productsToShow = MutableStateFlow<List<ProductListItem>>(emptyList())
    val productsToShow: StateFlow<List<ProductListItem>> = _productsToShow

    // StateFlow for loading state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // StateFlow for error messages
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    // StateFlow for the checkout dialog visibility
    private val _isCheckoutDialogShown = MutableStateFlow(false)
    val isCheckoutDialogShown: StateFlow<Boolean> = _isCheckoutDialogShown

    // StateFlow for selected products
    private val _selectedProducts = MutableStateFlow<List<ProductListItem>>(emptyList())
    val selectedProducts: StateFlow<List<ProductListItem>> = _selectedProducts

    private val _isAllSelected = MutableStateFlow(false)
    val isAllSelected: StateFlow<Boolean> = _isAllSelected

    private val _exportState = MutableStateFlow(AffectationExportState())
    val exportState: StateFlow<AffectationExportState> = _exportState

    private var exportJob: Job? = null

    // Current structure name
    private var structureName: String = ""

    private val accessorySelections = MutableStateFlow<Map<Int, Set<AccessoryType>>>(emptyMap())

    init {
        viewModelScope.launch {
            orderRepository.observeProductAccessories()
                .collect { selections ->
                    accessorySelections.value =
                        selections.associate { it.productId to it.selectedTypes }
                    setupProductsToShow()
                }
        }
    }

    // Initialize the product list for a specific structure
    fun initProductList(structureName: String) {
        viewModelScope.launch {
            _isLoading.value = true
            orderRepository.getProductsForStructure(structureName)
                .catch { e ->
                    _errorMessage.value = "Error fetching products: ${e.message}"
                    _isLoading.value = false
                }
                .collect { productList ->
                    _products.value = productList
                    this@OrderChooseProductsViewModel.structureName = structureName
                    setupProductsToShow()
                    _isLoading.value = false
                    _isAllSelected.value = false
                    _selectedProducts.value = emptyList()
                }
        }
    }

    fun onAccessoryToggle(productId: Int, accessoryType: AccessoryType, isSelected: Boolean) {
        viewModelScope.launch {
            val currentSelections = accessorySelections.value[productId] ?: emptySet()
            val updatedSelections = if (isSelected) {
                currentSelections + accessoryType
            } else {
                currentSelections - accessoryType
            }

            accessorySelections.update { currentMap ->
                val mutable = currentMap.toMutableMap()
                if (updatedSelections.isEmpty()) {
                    mutable.remove(productId)
                } else {
                    mutable[productId] = updatedSelections
                }
                mutable
            }

            _productsToShow.update { currentList ->
                currentList.map { item ->
                    if (item.id == productId) item.copy(accessories = updatedSelections) else item
                }
            }

            orderRepository.updateProductAccessories(productId, updatedSelections)
        }
    }

    // Update the search query and refresh the products to show
    fun onProductSearchQueryChange(newName: String) {
        _searchQuery.value = newName
        setupProductsToShow()
    }

    // Setup the products to show (apply filters and sorting)
    private fun setupProductsToShow() {
        val query = _searchQuery.value.trim()
        val filteredProducts = if (query.isBlank()) {
            _products.value
        } else {
            _products.value.filter { product ->
                product.name.contains(query, ignoreCase = true) ||
                        product.label.contains(query, ignoreCase = true) ||
                        product.structureName?.contains(query, ignoreCase = true) == true ||
                        product.assignedPersonnelName?.contains(query, ignoreCase = true) == true
            }
        }
        val sortedProducts = sortListByNameUseCase(filteredProducts)
        val previousItems = _productsToShow.value.associateBy { it.id }
        _productsToShow.value = sortedProducts.mapNotNull { product ->
            val productId = product.productId ?: return@mapNotNull null
            val accessories =
                accessorySelections.value[productId] ?: previousItems[productId]?.accessories
                ?: emptySet()
            val selectedItem = _selectedProducts.value.firstOrNull { it.id == productId }
            val baseItem = if (selectedItem != null) {
                product.toProductListItem().copy(selectedAmount = selectedItem.selectedAmount)
            } else {
                product.toProductListItem()
            }
            baseItem.copy(
                isExpanded = previousItems[productId]?.isExpanded ?: baseItem.isExpanded,
                accessories = accessories
            )
        }
        updateSelectAllState()
    }

    // Handle list item click (expand/collapse)
    fun onListItemClick(productId: Int) {
        val index = getIndexOfProduct(productId)
        if (index < 0) return

        val updatedList = _productsToShow.value.toMutableList()
        updatedList[index] = updatedList[index].copy(
            isExpanded = !updatedList[index].isExpanded
        )
        _productsToShow.value = updatedList
    }

    // Handle plus button click (increase selected amount)
    fun onPlusClick(productId: Int) {
        val index = getIndexOfProduct(productId)
        if (index < 0) return

        val updatedList = _productsToShow.value.toMutableList()
        val currentSelectionAmount = updatedList[index].selectedAmount

        updatedList[index] = updatedList[index].copy(
            selectedAmount = currentSelectionAmount + 1
        )
        _productsToShow.value = updatedList

        val selectedItem = updatedList[index]
        val currentSelectedProducts = _selectedProducts.value.toMutableList()
        if (currentSelectionAmount == 0) {
            currentSelectedProducts.add(selectedItem)
        } else {
            val updatedSelectedProducts = currentSelectedProducts.map {
                if (it.id == productId) {
                    it.copy(selectedAmount = it.selectedAmount + 1)
                } else {
                    it
                }
            }
            currentSelectedProducts.clear()
            currentSelectedProducts.addAll(updatedSelectedProducts)
        }
        _selectedProducts.value = currentSelectedProducts
        updateSelectAllState()
    }

    // Handle minus button click (decrease selected amount)
    fun onMinusClick(productId: Int) {
        val index = getIndexOfProduct(productId)
        if (index < 0) return

        val updatedList = _productsToShow.value.toMutableList()
        val currentSelectionAmount = updatedList[index].selectedAmount

        if (currentSelectionAmount == 0) return

        updatedList[index] = updatedList[index].copy(
            selectedAmount = currentSelectionAmount - 1
        )
        _productsToShow.value = updatedList

        val currentSelectedProducts = _selectedProducts.value.toMutableList()
        if (currentSelectionAmount == 1) {
            currentSelectedProducts.removeAll { it.id == productId }
        } else {
            val updatedSelectedProducts = currentSelectedProducts.map {
                if (it.id == productId) {
                    it.copy(selectedAmount = it.selectedAmount - 1)
                } else {
                    it
                }
            }
            currentSelectedProducts.clear()
            currentSelectedProducts.addAll(updatedSelectedProducts)
        }
        _selectedProducts.value = currentSelectedProducts
    }

    // Show the checkout dialog
    fun onCheckoutClick() {
        _isCheckoutDialogShown.value = true
    }

    // Dismiss the checkout dialog
    fun onDismissCheckoutDialog() {
        _isCheckoutDialogShown.value = false
    }

    // Confirm the order
    fun onBuy() {
        viewModelScope.launch {
            try {
                confirmOrderUseCase(
                    _selectedProducts.value.map { it.toBoughtProduct() },
                    structureName = structureName
                )

                _selectedProducts.value = emptyList()
                _productsToShow.update { currentList ->
                    currentList.map { item ->
                        item.copy(selectedAmount = 0)
                    }
                }
                _isCheckoutDialogShown.value = false
                _errorMessage.value = null
                _isAllSelected.value = false
            } catch (e: IllegalArgumentException) {
                _errorMessage.value = e.message
                _isCheckoutDialogShown.value = false
            } catch (e: Exception) {
                _errorMessage.value = "Erreur lors de la confirmation : ${e.message}"
                _isCheckoutDialogShown.value = false
            }
        }
    }

    fun consumeErrorMessage() {
        _errorMessage.value = null
    }

    fun onExportSelection() {
        val currentSelection = _selectedProducts.value
        if (currentSelection.isEmpty()) {
            _exportState.value = AffectationExportState(
                errorMessage = "Aucun produit sélectionné pour l'export."
            )
            return
        }

        _exportState.value = AffectationExportState(
            isExporting = true,
            progressPercentage = 0
        )

        exportJob?.cancel()

        val exportProducts = currentSelection.map { item ->
            val accessoriesSummary = if (item.accessories.isNotEmpty()) {
                item.accessories.joinToString(
                    separator = ", "
                ) { type ->
                    type.name.lowercase(Locale.getDefault())
                        .replaceFirstChar { it.titlecase(Locale.getDefault()) }
                }
            } else null

            val enrichedLabelParts = mutableListOf<String>()
            if (item.label.isNotBlank()) {
                enrichedLabelParts += item.label
            }
            item.structureName?.let { enrichedLabelParts += "Structure : $it" }
            item.assignedPersonnelName?.let { enrichedLabelParts += "Personnel : $it" }
            accessoriesSummary?.let { enrichedLabelParts += "Accessoires : $it" }

            val enrichedLabel = enrichedLabelParts.joinToString(separator = " | ")

            val base = item.toBoughtProduct()
            base.copy(label = if (enrichedLabel.isBlank()) base.label else enrichedLabel)
        }

        val exportOrder = Order(
            orderId = UUID.randomUUID().toString(),
            date = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault()).format(Date()),
            structureName = structureName,
            deliveryTime = currentSelection.firstOrNull()?.assignedPersonnelName ?: "",
            products = exportProducts
        )

        exportJob = viewModelScope.launch {
            exportRepository.startExportData(listOf(exportOrder)).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _exportState.update { state ->
                            state.copy(
                                isExporting = true,
                                progressPercentage = result.data?.progressPercentage ?: state.progressPercentage
                            )
                        }
                    }

                    is Resource.Success -> {
                        val path = result.data.path
                        if (path != null) {
                            _exportState.value = AffectationExportState(
                                isExporting = false,
                                progressPercentage = 100,
                                exportFilePath = path,
                                isShareRequested = true
                            )
                        } else {
                            _exportState.value = AffectationExportState(
                                isExporting = false,
                                errorMessage = "Impossible de générer le fichier d'export."
                            )
                        }
                    }

                    is Resource.Error -> {
                        _exportState.value = AffectationExportState(
                            isExporting = false,
                            errorMessage = result.errorMessage ?: "Erreur inconnue lors de l'export."
                        )
                    }
                }
            }
        }
    }

    fun onSelectAllToggle(selectAll: Boolean) {
        if (_productsToShow.value.isEmpty()) {
            _isAllSelected.value = false
            return
        }

        if (selectAll) {
            val updatedList = _productsToShow.value.map { item ->
                if (item.selectedAmount > 0) item else item.copy(selectedAmount = 1)
            }
            _productsToShow.value = updatedList
            _selectedProducts.value = updatedList.filter { it.selectedAmount > 0 }
        } else {
            _productsToShow.update { currentList ->
                currentList.map { it.copy(selectedAmount = 0) }
            }
            _selectedProducts.value = emptyList()
        }

        updateSelectAllState()
    }

    fun consumeExportError() {
        _exportState.update { it.copy(errorMessage = null) }
    }

    fun onExportShareHandled() {
        _exportState.update { it.copy(isShareRequested = false) }
    }

    private fun getIndexOfProduct(productId: Int): Int {
        return _productsToShow.value.indexOfFirst { it.id == productId }
    }

    private fun updateSelectAllState() {
        val currentList = _productsToShow.value
        _isAllSelected.value = currentList.isNotEmpty() && currentList.all { it.selectedAmount > 0 }
    }
}
