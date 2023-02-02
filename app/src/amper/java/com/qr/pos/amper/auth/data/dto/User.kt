package com.qr.pos.amper.auth.data.dto

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val id: String? = null,
    var firstName: String? = null,
    var lastName: String? = null,
    var email: String? = null,
    var mobile: String? = null,
    var userTypeKey: Int? = null,
    var userType: String? = null
) : Parcelable
