package com.qr.pos.amper.auth.data.dto

data class ActivationModel(
    val id: String? = null,
    val duration_days: Int? = null,
    val used: Boolean? = null,
    val startDate: Long? = null,
    val endDate: Long? = null,
    val voidCode: String? = null
)
