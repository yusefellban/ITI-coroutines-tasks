package iti.yousef.coroutinesAndorid.data.model

import com.google.gson.annotations.SerializedName

data class Product(
    val id: Int,
    val title: String,
    val description: String,
    val price: Double,
    val discountPercentage: Double,
    val rating: Double,
    val stock: Int,
    val brand: String?,
    val category: String,
    val thumbnail: String,
    @SerializedName("images")
    val images: List<String>
)
