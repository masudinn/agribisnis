package com.project.kebunmade.homepage.order

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore

class OrderViewModel : ViewModel() {
    private val orderList = MutableLiveData<ArrayList<OrderModel>>()
    private val listItems = ArrayList<OrderModel>()
    private val TAG = OrderViewModel::class.java.simpleName

    fun setListOrderProcessByAll() {
        listItems.clear()

        try {
            FirebaseFirestore.getInstance().collection("order")
                .whereNotIn("status", listOf("Ditolak", "Selesai"))
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val model = OrderModel()
                        model.address = document.data["address"].toString()
                        model.date = document.data["date"].toString()
                        model.image = document.data["image"].toString()
                        model.orderId = document.data["orderId"].toString()
                        model.status = document.data["status"].toString()
                        model.userId = document.data["userId"].toString()
                        model.userName = document.data["userName"].toString()
                        model.totalPrice = document.data["totalPrice"] as Long
                        model.product = document.toObject(OrderModel::class.java).product
                        model.ongkir = document.data["ongkir"] as Long
                        model.isOngkirActive = document.data["isOngkirActive"] as Boolean
                        model.paymentProof = document.data["paymentProof"].toString()

                        listItems.add(model)
                    }
                    orderList.postValue(listItems)
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }
        } catch (error: Exception) {
            error.printStackTrace()
        }
    }

    fun setListOrderSuccessByAll() {
        listItems.clear()

        try {
            FirebaseFirestore.getInstance().collection("order")
                .whereEqualTo("status", "Selesai")
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val model = OrderModel()
                        model.address = document.data["address"].toString()
                        model.date = document.data["date"].toString()
                        model.image = document.data["image"].toString()
                        model.orderId = document.data["orderId"].toString()
                        model.status = document.data["status"].toString()
                        model.userId = document.data["userId"].toString()
                        model.userName = document.data["userName"].toString()
                        model.totalPrice = document.data["totalPrice"] as Long
                        model.product = document.toObject(OrderModel::class.java).product
                        model.ongkir = document.data["ongkir"] as Long
                        model.isOngkirActive = document.data["isOngkirActive"] as Boolean
                        model.paymentProof = document.data["paymentProof"].toString()

                        listItems.add(model)
                    }
                    orderList.postValue(listItems)
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }
        } catch (error: Exception) {
            error.printStackTrace()
        }
    }

    fun setListOrderFailureByAll() {
        listItems.clear()

        try {
            FirebaseFirestore.getInstance().collection("order")
                .whereEqualTo("status", "Ditolak")
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val model = OrderModel()
                        model.address = document.data["address"].toString()
                        model.date = document.data["date"].toString()
                        model.image = document.data["image"].toString()
                        model.orderId = document.data["orderId"].toString()
                        model.status = document.data["status"].toString()
                        model.userId = document.data["userId"].toString()
                        model.userName = document.data["userName"].toString()
                        model.totalPrice = document.data["totalPrice"] as Long
                        model.product = document.toObject(OrderModel::class.java).product
                        model.ongkir = document.data["ongkir"] as Long
                        model.isOngkirActive = document.data["isOngkirActive"] as Boolean
                        model.paymentProof = document.data["paymentProof"].toString()
                        listItems.add(model)
                    }
                    orderList.postValue(listItems)
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }
        } catch (error: Exception) {
            error.printStackTrace()
        }
    }

    fun setListOrderByIDProcess(userId: String) {
        listItems.clear()

        try {
            FirebaseFirestore.getInstance().collection("order")
                .whereEqualTo("userId", userId)
                .whereNotIn("status", listOf("Ditolak", "Selesai"))
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val model = OrderModel()
                        model.address = document.data["address"].toString()
                        model.date = document.data["date"].toString()
                        model.image = document.data["image"].toString()
                        model.orderId = document.data["orderId"].toString()
                        model.status = document.data["status"].toString()
                        model.userId = document.data["userId"].toString()
                        model.userName = document.data["userName"].toString()
                        model.totalPrice = document.data["totalPrice"] as Long
                        model.product = document.toObject(OrderModel::class.java).product
                        model.ongkir = document.data["ongkir"] as Long
                        model.isOngkirActive = document.data["isOngkirActive"] as Boolean
                        model.paymentProof = document.data["paymentProof"].toString()
                        listItems.add(model)
                    }
                    orderList.postValue(listItems)
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }
        } catch (error: Exception) {
            error.printStackTrace()
        }
    }

    fun setListOrderByIDSuceess(userId: String) {
        listItems.clear()

        try {
            FirebaseFirestore.getInstance().collection("order")
                .whereEqualTo("userId", userId)
                .whereEqualTo("status", "Selesai")
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val model = OrderModel()
                        model.address = document.data["address"].toString()
                        model.date = document.data["date"].toString()
                        model.image = document.data["image"].toString()
                        model.orderId = document.data["orderId"].toString()
                        model.status = document.data["status"].toString()
                        model.userId = document.data["userId"].toString()
                        model.userName = document.data["userName"].toString()
                        model.totalPrice = document.data["totalPrice"] as Long
                        model.product = document.toObject(OrderModel::class.java).product
                        model.ongkir = document.data["ongkir"] as Long
                        model.isOngkirActive = document.data["isOngkirActive"] as Boolean
                        model.paymentProof = document.data["paymentProof"].toString()
                        listItems.add(model)
                    }
                    orderList.postValue(listItems)
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }
        } catch (error: Exception) {
            error.printStackTrace()
        }
    }

    fun setListOrderByIDFailure(userId: String) {
        listItems.clear()

        try {
            FirebaseFirestore.getInstance().collection("order")
                .whereEqualTo("userId", userId)
                .whereEqualTo("status", "Ditolak")
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val model = OrderModel()
                        model.address = document.data["address"].toString()
                        model.date = document.data["date"].toString()
                        model.image = document.data["image"].toString()
                        model.orderId = document.data["orderId"].toString()
                        model.status = document.data["status"].toString()
                        model.userId = document.data["userId"].toString()
                        model.userName = document.data["userName"].toString()
                        model.totalPrice = document.data["totalPrice"] as Long
                        model.product = document.toObject(OrderModel::class.java).product
                        model.ongkir = document.data["ongkir"] as Long
                        model.isOngkirActive = document.data["isOngkirActive"] as Boolean
                        model.paymentProof = document.data["paymentProof"].toString()
                        listItems.add(model)
                    }
                    orderList.postValue(listItems)
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }
        } catch (error: Exception) {
            error.printStackTrace()
        }
    }

    fun getOrderList() : LiveData<ArrayList<OrderModel>> {
        return orderList
    }
}