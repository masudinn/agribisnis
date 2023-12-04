package com.project.kebunmade.homepage.chat.message

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.QuerySnapshot
import androidx.lifecycle.LiveData


class MessageViewModel : ViewModel() {
    private val messageList = MutableLiveData<ArrayList<MessageModel>>()
    private val listItems = ArrayList<MessageModel>()
    private val TAG = MessageViewModel::class.java.simpleName

    fun setListMessage(userId : String) {
        listItems.clear()

        try {
            FirebaseFirestore
                .getInstance()
                .collection("chat")
                .document(userId)
                .collection("message")
                .get()
                .addOnCompleteListener { task: Task<QuerySnapshot> ->
                    if (task.isSuccessful) {
                        for (document in task.result) {
                            val model = MessageModel()
                            model.message = "" + document["message"]
                            model.dateTime = "" + document["dateTime"]
                            model.userId = "" + document["userId"]
                            model.role = "" + document["role"]
                            model.isText = document["isText"] as Boolean

                            listItems.add(model)
                        }
                        messageList.postValue(listItems)
                    } else {
                        Log.e(TAG, task.toString())
                    }
                }
        } catch (error: Exception) {
            error.printStackTrace()
        }
    }

    fun getMessageList(): LiveData<ArrayList<MessageModel>> {
        return messageList
    }

}