package com.project.kebunmade.homepage.chat

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ChatModel(
    var customerId: String? = null,
    var customerName: String? = null,
    var dateTime: String? = null,
    var lastMessage: String? = null,
) : Parcelable