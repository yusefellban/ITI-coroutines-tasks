package iti.yousef.coroutinesAndorid.data

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import iti.yousef.coroutinesAndorid.data.local.ProductDatabase
import iti.yousef.coroutinesAndorid.data.local.ProductEntity
import iti.yousef.coroutinesAndorid.data.model.Product
import iti.yousef.coroutinesAndorid.data.remote.RetrofitHelper

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

data class DataResult<T>(
    val data: T,
    val isOffline: Boolean
)

class ProductRepository(private val context: Context) {

    private val dao = ProductDatabase.getInstance(context).productDao()
    private val api = RetrofitHelper.productApiService

    fun getProducts(): Flow<DataResult<List<Product>>> = flow {
        try {
            if (isNetworkAvailable()) {
                android.util.Log.d("ProductsApp", "Network is available, fetching from API")
                val response = api.getProducts()
                android.util.Log.d("ProductsApp", "API Response: ${response.products.size} items")
                val entities = response.products.map { it.toEntity() }
                dao.deleteAll()
                dao.insertAll(entities)
            } else {
                android.util.Log.d("ProductsApp", "No network, skipping API fetch")
            }
        } catch (e: Exception) {
            android.util.Log.d("ProductsApp", "Error fetching from API: ${e.message}")
        }

        dao.getAllProducts().collect { entities ->
            val cached = entities.map { it.toProduct() }
            android.util.Log.d("ProductsApp", "Room Response: ${cached.size} items")
            emit(DataResult(cached, isOffline = !isNetworkAvailable()))
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}

// ---- Mapping extensions ----

fun Product.toEntity() = ProductEntity(
    id = id,
    title = title,
    description = description,
    price = price,
    discountPercentage = discountPercentage,
    rating = rating,
    stock = stock,
    brand = brand,
    category = category,
    thumbnail = thumbnail
)

fun ProductEntity.toProduct() = Product(
    id = id,
    title = title,
    description = description,
    price = price,
    discountPercentage = discountPercentage,
    rating = rating,
    stock = stock,
    brand = brand,
    category = category,
    thumbnail = thumbnail,
    images = listOf(thumbnail)
)
