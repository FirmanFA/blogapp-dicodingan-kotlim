package com.dicodingan.dicodingan.model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class User (
    var email: String? = "",
    var uid: String? = "",
    var username: String? = "",
    var profile: UserProfile? = null

)