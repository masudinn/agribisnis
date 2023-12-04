package com.project.kebunmade.homepage.chat

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore

class ChatViewModel : ViewModel() {

    private val chatList = MutableLiveData<ArrayList<ChatModel>>()
    private val listItems = ArrayList<ChatModel>()
    private val TAG = ChatViewModel::class.java.simpleName

    fun setListChatByCustomerSide(customerId: String) {
        listItems.clear()

        try {
            FirebaseFirestore.getInstance().collection("chat")
                .whereEqualTo("customerId", customerId)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val model = ChatModel()
                        model.customerId = document.data["customerId"].toString()
                        model.customerName = document.data["customerName"].toString()
                        model.dateTime = document.data["dateTime"].toString()
                        model.lastMessage = document.data["lastMessage"].toString()

                        listItems.add(model)
                    }
                    chatList.postValue(listItems)
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }
        } catch (error: Exception) {
            error.printStackTrace()
        }
    }

    fun setListChatByAdminSide() {
        listItems.clear()

        try {
            FirebaseFirestore.getInstance().collection("chat")
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val model = ChatModel()
                        model.customerId = document.data["customerId"].toString()
                        model.customerName = document.data["customerName"].toString()
                        model.dateTime = document.data["dateTime"].toString()
                        model.lastMessage = document.data["lastMessage"].toString()

                        listItems.add(model)
                    }
                    chatList.postValue(listItems)
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }
        } catch (error: Exception) {
            error.printStackTrace()
        }
    }

    fun getChatList() : LiveData<ArrayList<ChatModel>> {
        return chatList
    }

}