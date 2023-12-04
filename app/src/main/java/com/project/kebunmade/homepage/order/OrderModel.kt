package com.project.kebunmade.homepage.order

import android.os.Parcelable
import com.project.kebunmade.homepage.product.cart.CartModel
import com.project.kebunmade.homepage.product.product.ProductModel
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class OrderModel(
    var address: String? = null,
    var date: String? = null,
    var image: String? = null,
    var orderId: String? = null,
    var product: List<CartModel>? = null,
    var status: String? = null,
    var totalPrice: Long? = 0L,
    var userId: String? = null,
    var userName: String? = null,
    var ongkir: Long? = 0L,
    var isOngkirActive: Boolean? = false,
    var paymentProof: String? = null,
) : Parcelable