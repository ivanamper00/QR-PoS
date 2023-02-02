package com.qr.pos.amper.common.domain.repository

import com.qr.pos.amper.auth.data.dto.User
import com.qr.pos.amper.auth.utils.UserType


interface SharedPrefRepo {
    fun setDomain(domain: String)
    fun getDomain(): String
    fun setUser(user: User)
    fun getUser(): User
    fun getUserType(): UserType
    fun getServerDate(): String
    fun setServerDate(dateStr: String)
}