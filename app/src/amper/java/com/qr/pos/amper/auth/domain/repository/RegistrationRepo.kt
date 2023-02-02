package com.qr.pos.amper.auth.domain.repository

import com.qr.pos.amper.auth.data.dto.User
import com.qr.pos.amper.utils.network.NetResponse
import kotlinx.coroutines.flow.Flow

interface RegistrationRepo {

    suspend fun validateEmail(email: String) : Flow<NetResponse<Boolean>>

    suspend fun registerUser(user: User, password: String): Flow<NetResponse<User>>
}