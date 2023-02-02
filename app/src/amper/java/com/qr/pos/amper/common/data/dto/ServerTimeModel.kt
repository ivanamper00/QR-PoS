package com.qr.pos.amper.common.data.dto

data class ServerTimeModel(
    val date: String? = null,
    val dateTime: String? = null,
    val day: Int? = null,
    val dayOfWeek: String? = null,
    val dstActive: Boolean? = null,
    val hour: Int? = null,
    val milliSeconds: Int? = null,
    val minute: Int? = null,
    val month: Int? = null,
    val seconds: Int? = null,
    val time: String? = null,
    val timeZone: String? = null,
    val year: Int? = null
)