package com.farouktouil.farouktouil.order_feature.presentation

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.farouktouil.farouktouil.core.domain.model.Product
import com.farouktouil.farouktouil.order_feature.domain.repository.OrderRepository
import com.farouktouil.farouktouil.order_feature.domain.use_case.ConfirmOrderUseCase
import com.farouktouil.farouktouil.order_feature.domain.use_case.FilterListByNameUseCase
import com.farouktouil.farouktouil.order_feature.domain.use_case.SortListByNameUseCase
import com.farouktouil.farouktouil.order_feature.presentation.mapper.toBoughtProduct
import com.farouktouil.farouktouil.order_feature.presentation.mapper.toProductListItem
import com.farouktouil.farouktouil.order_feature.presentation.state.ProductListItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.properties.Delegates
@HiltViewModel
class OrderChooseProductsViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
    private val filterListByNameUseCase: FilterListByNameUseCase,
    private val sortListByNameUseCase: SortListByNameUseCase,
    private val confirmOrderUseCase: ConfirmOrderUseCase
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

    // Current deliverer ID
    private var delivererId: Int = -1

    // Initialize the product list for a specific deliverer
    fun initProductList(delivererId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            orderRepository.getProductsForDeliverer(delivererId)
                .catch { e ->
                    _errorMessage.value = "Error fetching products: ${e.message}"
                }
                .collect { productList ->
                    _products.value = productList
                    this@OrderChooseProductsViewModel.delivererId = delivererId
                    setupProductsToShow()
                    _isLoading.value = false
                }
        }
    }

    // Update the search query and refresh the products to show
    fun onProductSearchQueryChange(newName: String) {
        _searchQuery.value = newName
        setupProductsToShow()
    }

    // Setup the products to show (apply filters and sorting)
    private fun setupProductsToShow() {
        val filteredProducts = filterListByNameUseCase(_products.value, _searchQuery.value)
        val sortedProducts = sortListByNameUseCase(filteredProducts)
        _productsToShow.value = sortedProducts.map { product ->
            val selectedItem = _selectedProducts.value.firstOrNull { it.id == product.productId }
            if (selectedItem != null) {
                product.toProductListItem().copy(selectedAmount = selectedItem.selectedAmount)
            } else {
                product.toProductListItem()
            }
        }
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

    // Get the index of a product by its ID
    private fun getIndexOfProduct(productId: Int): Int {
        return _productsToShow.value.indexOfFirst { it.id == productId }
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
            confirmOrderUseCase(
                _selectedProducts.value.map { it.toBoughtProduct() },
                delivererId = delivererId
            )
        }
    }
}