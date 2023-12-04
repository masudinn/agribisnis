package com.project.kebunmade.homepage.chat.message

data class MessageModel(
    var message: String? = null,
    var userId: String? = null,
    var dateTime: String? = null,
    var isText: Boolean? = true,
    var role: String? = null
)