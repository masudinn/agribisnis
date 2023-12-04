package com.project.kebunmade.homepage.product.cart

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CartModel(
    var cartId: String? = null,
    var userId: String? = null,
    var name: String? = null,
    var qty: Long? = 0,
    var price: Long? = 0,
    var description: String? = null,
    var image: String? = null,
    var productId: String? = null,
    var category: String? = null,
) : Parcelable