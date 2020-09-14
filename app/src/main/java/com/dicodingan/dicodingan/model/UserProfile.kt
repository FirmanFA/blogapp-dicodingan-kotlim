package com.dicodingan.dicodingan.model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class UserProfile(
    var birthDate:String? = "",
    var displayName:String? = "",
    var profileImageUrl:String? = ""
)