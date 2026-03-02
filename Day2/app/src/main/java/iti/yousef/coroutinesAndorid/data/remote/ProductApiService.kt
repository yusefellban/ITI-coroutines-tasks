package iti.yousef.coroutinesAndorid.data.remote

import iti.yousef.coroutinesAndorid.data.model.ProductsResponse
import retrofit2.http.GET

interface ProductApiService {

    @GET("products?limit=100")
    suspend fun getProducts(): ProductsResponse
}
