package com.project.kebunmade.homepage.product.cart

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore

class CartViewModel : ViewModel() {

    private val cartList = MutableLiveData<ArrayList<CartModel>>()
    private val listItems = ArrayList<CartModel>()
    private val TAG = CartViewModel::class.java.simpleName

    fun setListCart() {
        listItems.clear()


        try {
            FirebaseFirestore.getInstance().collection("cart")
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val model = CartModel()
                        model.category = document.data["category"].toString()
                        model.userId = document.data["usetId"].toString()
                        model.image = document.data["image"].toString()
                        model.name = document.data["name"].toString()
                        model.description = document.data["description"].toString()
                        model.price = document.data["price"] as Long
                        model.qty = document.data["qty"] as Long
                        model.productId = document.data["productId"].toString()
                        model.cartId = document.data["cartId"].toString()

                        listItems.add(model)
                    }
                    cartList.postValue(listItems)
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }
        } catch (error: Exception) {
            error.printStackTrace()
        }
    }

    fun getCartList() : LiveData<ArrayList<CartModel>> {
        return cartList
    }

}