package com.dicodingan.dicodingan.model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class UserDetail(
    var birthDate:String? = "",
    var displayName:String? = "",
    var phoneNumber:String? = "",
    var uid:String? = ""
)