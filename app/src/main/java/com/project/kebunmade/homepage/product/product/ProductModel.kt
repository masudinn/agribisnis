package com.project.kebunmade.homepage.product.product

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProductModel(
    var name: String? = null,
    var description: String? = null,
    var price: Long? = null,
    var image: String? = null,
    var productId: String? = null,
    var category: String? = null,
    var info: String? = null,
    var caraPenyimpanan: String? = null,
) : Parcelable