package com.project.kebunmade.homepage.product.product

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore

class ProductViewModel : ViewModel() {

    private val productList = MutableLiveData<ArrayList<ProductModel>>()
    private val listItems = ArrayList<ProductModel>()
    private val TAG = ProductViewModel::class.java.simpleName

    fun setListProduct(category: String) {
        listItems.clear()


        try {
            FirebaseFirestore.getInstance().collection("product")
                .whereEqualTo("category", category)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val model = ProductModel()
                        model.category = document.data["category"].toString()
                        model.productId = document.data["productId"].toString()
                        model.image = document.data["image"].toString()
                        model.price = document.data["price"].toString().toLong()
                        model.name = document.data["name"].toString()
                        model.description = document.data["description"].toString()
                        model.info = document.data["info"].toString()
                        model.caraPenyimpanan = document.data["caraPenyimpanan"].toString()

                        listItems.add(model)
                    }
                    productList.postValue(listItems)
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }
        } catch (error: Exception) {
            error.printStackTrace()
        }
    }

    fun setListProductAll() {
        listItems.clear()


        try {
            FirebaseFirestore.getInstance().collection("product")
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val model = ProductModel()
                        model.category = document.data["category"].toString()
                        model.productId = document.data["productId"].toString()
                        model.image = document.data["image"].toString()
                        model.price = document.data["price"].toString().toLong()
                        model.name = document.data["name"].toString()
                        model.description = document.data["description"].toString()
                        model.info = document.data["info"].toString()
                        model.caraPenyimpanan = document.data["caraPenyimpanan"].toString()

                        listItems.add(model)
                    }
                    productList.postValue(listItems)
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }
        } catch (error: Exception) {
            error.printStackTrace()
        }
    }

    fun setListProductAllByQuery(productName: String) {
        listItems.clear()


        try {
            FirebaseFirestore.getInstance().collection("product")
                .whereGreaterThanOrEqualTo("nameTemp", productName)
                .whereLessThanOrEqualTo("nameTemp", productName + '\uf8ff')
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val model = ProductModel()
                        model.category = document.data["category"].toString()
                        model.productId = document.data["productId"].toString()
                        model.image = document.data["image"].toString()
                        model.price = document.data["price"].toString().toLong()
                        model.name = document.data["name"].toString()
                        model.description = document.data["description"].toString()
                        model.info = document.data["info"].toString()
                        model.caraPenyimpanan = document.data["caraPenyimpanan"].toString()

                        listItems.add(model)
                    }
                    productList.postValue(listItems)
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }
        } catch (error: Exception) {
            error.printStackTrace()
        }
    }

    fun setListProductByQuery(category: String, productName: String) {
        listItems.clear()


        try {
            FirebaseFirestore.getInstance().collection("product")
                .whereEqualTo("category", category)
                .whereGreaterThanOrEqualTo("nameTemp", productName)
                .whereLessThanOrEqualTo("nameTemp", productName + '\uf8ff')
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val model = ProductModel()
                        model.category = document.data["category"].toString()
                        model.productId = document.data["productId"].toString()
                        model.image = document.data["image"].toString()
                        model.price = document.data["price"].toString().toLong()
                        model.name = document.data["name"].toString()
                        model.description = document.data["description"].toString()
                        model.info = document.data["info"].toString()
                        model.caraPenyimpanan = document.data["caraPenyimpanan"].toString()

                        listItems.add(model)
                    }
                    productList.postValue(listItems)
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }
        } catch (error: Exception) {
            error.printStackTrace()
        }
    }

    fun setListProductDetail(category: String, productId: String) {
        listItems.clear()


        try {
            FirebaseFirestore.getInstance().collection("product")
                .whereNotEqualTo("productId", productId)
                .whereEqualTo("category", category)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val model = ProductModel()
                        model.category = document.data["category"].toString()
                        model.productId = document.data["productId"].toString()
                        model.image = document.data["image"].toString()
                        model.price = document.data["price"].toString().toLong()
                        model.name = document.data["name"].toString()
                        model.description = document.data["description"].toString()
                        model.info = document.data["info"].toString()
                        model.caraPenyimpanan = document.data["caraPenyimpanan"].toString()

                        listItems.add(model)
                    }
                    productList.postValue(listItems)
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }
        } catch (error: Exception) {
            error.printStackTrace()
        }
    }

    fun getProductList(): LiveData<ArrayList<ProductModel>> {
        return productList
    }


}