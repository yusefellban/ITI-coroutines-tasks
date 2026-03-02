package iti.yousef.coroutinesAndorid.presentation.view

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.Coil
import coil.ImageLoader
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.request.CachePolicy
import iti.yousef.coroutinesAndorid.data.ProductRepository
import iti.yousef.coroutinesAndorid.data.model.Product
import iti.yousef.coroutinesAndorid.presentation.view.ui.screens.ProductDetailScreen
import iti.yousef.coroutinesAndorid.presentation.view.ui.screens.ProductListScreen
import iti.yousef.coroutinesAndorid.presentation.view.ui.theme.CoroutinesAndoridTheme
import iti.yousef.coroutinesAndorid.presentation.viewModel.ProductFactory
import iti.yousef.coroutinesAndorid.presentation.viewModel.ProductsViewModel

class MainActivity : ComponentActivity() {

    private lateinit var viewModel: ProductsViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Set up Coil ImageLoader for aggressive caching
        val imageLoader = ImageLoader.Builder(this)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .memoryCache {
                MemoryCache.Builder(this)
                    .maxSizePercent(0.25)
                    .build()
            }
            .diskCachePolicy(CachePolicy.ENABLED)
            .diskCache {
                DiskCache.Builder()
                    .directory(cacheDir.resolve("image_cache"))
                    .maxSizeBytes(50L * 1024 * 1024) // 50MB disk cache
                    .build()
            }
            .crossfade(true)
            .respectCacheHeaders(false) // Force cache even if headers say no
            .build()

        Coil.setImageLoader(imageLoader)


        val repository = ProductRepository(application)
        val factory = ProductFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(ProductsViewModel::class.java)

        setContent {

            CoroutinesAndoridTheme {
                val products by viewModel.allProducts.collectAsState()
                val isLoading by viewModel.isLoading.collectAsState()
                val isOffline by viewModel.isOffline.collectAsState()
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    ProductsApp(
                        products = products,
                        isLoading = isLoading,
                        isOffline = isOffline
                    )
                }
            }
        }
    }

}

@Composable
fun ProductsApp(
    products: List<Product>,
    isLoading: Boolean,
    isOffline: Boolean
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    if (isLandscape) {
        LandscapeLayout(products = products, isLoading = isLoading, isOffline = isOffline)
    } else {
        PortraitLayout(products = products, isLoading = isLoading, isOffline = isOffline)
    }
}

@Composable
fun PortraitLayout(products: List<Product>, isLoading: Boolean, isOffline: Boolean) {
    val navController = rememberNavController()
    var selectedProduct by remember { mutableStateOf<Product?>(null) }

    NavHost(navController = navController, startDestination = "list") {
        composable("list") {
            ProductListScreen(
                products = products,
                isLoading = isLoading,
                isOffline = isOffline,
                onProductClick = { product ->
                    selectedProduct = product
                    navController.navigate("detail")
                }
            )
        }
        composable("detail") {
            ProductDetailScreen(
                product = selectedProduct,
                onBack = { navController.popBackStack() }
            )
        }
    }
}







@Composable
fun LandscapeLayout(products: List<Product>, isLoading: Boolean, isOffline: Boolean) {
    var selectedProduct by remember { mutableStateOf<Product?>(null) }

    Row(modifier = Modifier.fillMaxSize()) {
        // List panel — 40% width
        ProductListScreen(
            products = products,
            isLoading = isLoading,
            isOffline = isOffline,
            onProductClick = { product -> selectedProduct = product },
            modifier = Modifier.weight(0.4f)
        )
        // Detail panel — 60% width
        ProductDetailScreen(
            product = selectedProduct,
            onBack = null, // No back button in landscape (side-by-side)
            modifier = Modifier.weight(0.6f)
        )
    }
}