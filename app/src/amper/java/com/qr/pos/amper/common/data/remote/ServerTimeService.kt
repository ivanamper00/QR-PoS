package com.qr.pos.amper.common.data.remote

import com.qr.pos.amper.common.data.dto.ServerTimeModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ServerTimeService {

    @GET("zone")
    suspend fun getServerTime(
        @Query("timeZone") timeZone: String
    ): Response<ServerTimeModel>
}