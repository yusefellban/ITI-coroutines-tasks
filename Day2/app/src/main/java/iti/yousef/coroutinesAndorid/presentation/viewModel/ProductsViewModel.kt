package iti.yousef.coroutinesAndorid.presentation.viewModel

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import iti.yousef.coroutinesAndorid.data.ProductRepository
import iti.yousef.coroutinesAndorid.data.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class ProductsViewModel(private val productRepo: ProductRepository) : ViewModel() {

//    private val productRepo = ProductRepository(application)

//    private val _products = mutableStateOf<List<Product>>(emptyList())
//    val allProducts: State<List<Product>> = _products

    //    private val _isLoading = mutableStateOf(false)
//    val isLoading: State<Boolean> = _isLoading
//
//    private val _isOffline = mutableStateOf(false)
//    val isOffline: State<Boolean> = _isOffline
    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val allProducts = _products.asStateFlow()
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _isOffline = MutableStateFlow(false)
    val isOffline = _isOffline.asStateFlow()


    init {
        loadProducts()
    }

    //    private fun loadProducts() {
//        viewModelScope.launch {
//            _isLoading.value = true
//            try {
//                productRepo.getProducts().collect { result ->
//                    _products.value = result.data ?: emptyList()
//                    _isOffline.value = result.isOffline
//                    _isLoading.value = false
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//                _products.value = emptyList()
//                _isLoading.value = false
//            }
//        }
//    }
    private fun loadProducts() {
        productRepo.getProducts()
            .onStart {
                _isLoading.value = true
            }
            .catch { e ->
                e.printStackTrace()
                _products.value = emptyList()
                _isOffline.value = true
                _isLoading.value = false
            }
            .onEach { result ->
                _products.value = result.data.orEmpty()
                _isOffline.value = result.isOffline
                _isLoading.value = false
            }
            .launchIn(viewModelScope)
    }


}


class ProductFactory(
    private val productRepo: ProductRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ProductsViewModel(productRepo) as T
    }
}
