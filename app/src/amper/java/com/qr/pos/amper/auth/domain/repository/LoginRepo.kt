package com.qr.pos.amper.auth.domain.repository

import com.qr.pos.amper.auth.data.dto.ActivationModel
import com.qr.pos.amper.auth.data.dto.SSOUser
import com.qr.pos.amper.auth.data.dto.User
import com.qr.pos.amper.utils.network.NetResponse
import kotlinx.coroutines.flow.Flow

interface LoginRepo {
    suspend fun login(username: String, password: String): Flow<NetResponse<SSOUser>>

    suspend fun login(idToken: String, user: User): Flow<NetResponse<SSOUser>>

    suspend fun getActivationDuration(domain: String): Flow<ActivationModel>

    suspend fun activateBilling(activationCode: String, domain: String) : Flow<ActivationModel>

    fun logout()
}