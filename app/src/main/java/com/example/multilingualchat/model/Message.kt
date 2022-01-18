package com.example.multilingualchat.model

data class Message(
    val sender: String? = null,
    val receiver: String? = null,
    var message: String? = null,
    var msgLang: String? = null
) {
    var isExpanded: Boolean = false
}