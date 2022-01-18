package com.example.multilingualchat.model

data class User(
    val uid: String? = null,
    val email: String? = null,
    var name: String? = null,
    val phoneNumber: String? = null,
    val profilePic: String? = null,
    val language: String? = null
) {


}