package com.dicodingan.dicodingan.model

data class Post (
    val content:String? = "",
    val author:String? = "",
    val dateCreated:String? = "",
    val images:ArrayList<String>? = null,
    val lastUpdated:String? = "",
    val thumbnail:String? = "",
    val title:String? = ""
)