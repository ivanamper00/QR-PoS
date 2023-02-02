package com.qr.pos.amper.common.data.repository

import com.qr.pos.amper.common.data.dto.ServerTimeModel
import com.qr.pos.amper.common.data.remote.ServerTimeService
import com.qr.pos.amper.common.domain.repository.SharedPrefRepo
import com.qr.pos.amper.common.domain.repository.TimeRepo
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import retrofit2.Response
import javax.inject.Inject

class ServerTimeRepoImp @Inject constructor(
    private val service: ServerTimeService,
    private val sf: SharedPrefRepo
): TimeRepo {
    override suspend fun getServerTime(
        timeZone: String
    ): Flow<ServerTimeModel> = callbackFlow {
        try {
            val result = service.getServerTime(timeZone).body()
            result?.date?.let {
                sf.setServerDate(it)
            }
            trySend(result!!)
            awaitClose()
        }catch (e: Exception){
            close(e)
        }
    }
}