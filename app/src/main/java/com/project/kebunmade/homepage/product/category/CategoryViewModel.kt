package com.project.kebunmade.homepage.product.category

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore

class CategoryViewModel : ViewModel() {

    private val categoryList = MutableLiveData<ArrayList<CategoryModel>>()
    private val listItems = ArrayList<CategoryModel>()
    private val TAG = CategoryViewModel::class.java.simpleName

    fun setListCategory() {
        listItems.clear()


        try {
            FirebaseFirestore.getInstance().collection("category")
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val model = CategoryModel()
                        model.category = document.data["category"].toString()
                        model.uid = document.data["uid"].toString()
                        model.image = document.data["image"].toString()

                        listItems.add(model)
                    }
                    categoryList.postValue(listItems)
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }
        } catch (error: Exception) {
            error.printStackTrace()
        }
    }

    fun getCategoryList() : LiveData<ArrayList<CategoryModel>> {
        return categoryList
    }
}