package com.qr.pos.amper.utils.network

sealed class NetResponse<out T>() {
    data class Success<T>(val data: T): NetResponse<T>()
    data class Error(val error: Throwable): NetResponse<Nothing>()
    object NullOrEmpty: NetResponse<Nothing>()
}