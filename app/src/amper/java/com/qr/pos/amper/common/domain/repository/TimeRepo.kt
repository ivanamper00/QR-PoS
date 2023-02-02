package com.qr.pos.amper.common.domain.repository

import com.qr.pos.amper.common.data.dto.ServerTimeModel
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface TimeRepo {
    suspend fun getServerTime(
        timeZone: String
    ): Flow<ServerTimeModel>
}