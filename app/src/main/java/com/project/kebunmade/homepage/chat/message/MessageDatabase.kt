package com.project.kebunmade.homepage.chat.message

import com.google.firebase.firestore.FirebaseFirestore

import android.util.Log
import com.google.android.gms.tasks.Task


object MessageDatabase {

    fun sendChat(message: String, dateTime: String, userId: String, isText: Boolean, customerId: String?, role: String) {

        val timeInMillis = System.currentTimeMillis().toString()

        val logChat: MutableMap<String, Any> = HashMap()
        logChat["message"] = message
        logChat["dateTime"] = dateTime
        logChat["userId"] = userId
        logChat["isText"] = isText
        logChat["role"] = role


        // UPDATE LOG CHAT (SISI PENGIRIM)
        FirebaseFirestore
            .getInstance()
            .collection("chat")
            .document(customerId!!)
            .collection("message")
            .document(timeInMillis)
            .set(logChat)
            .addOnCompleteListener { task: Task<Void?> ->
                if (task.isSuccessful) {
                    Log.d("SENDER MSG", "success")
                } else {
                    Log.d("SENDER MSG", task.toString())
                }
            }


        val lastMessage: MutableMap<String, Any> = HashMap()
        lastMessage["lastMessage"] = message
        lastMessage["dateTime"] = dateTime

        /// update last chat

        /// update last chat
        FirebaseFirestore
            .getInstance()
            .collection("chat")
            .document(customerId)
            .update(lastMessage)
            .addOnCompleteListener { task: Task<Void?> ->
                if (task.isSuccessful) {
                    Log.d("SENDER MSG", "success")
                } else {
                    Log.d("SENDER MSG", task.toString())
                }
            }


    }

}