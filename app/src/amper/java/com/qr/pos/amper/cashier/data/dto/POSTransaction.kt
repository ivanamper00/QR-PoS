package com.qr.pos.amper.cashier.data.dto

import com.qr.pos.amper.inventory.data.dto.Product
import com.qr.pos.amper.utils.DateUtils
import java.text.DateFormat
import java.util.*

data class POSTransaction(
    val id: String? = null,
    val date: Long? = Date().time,
    val agent: String? = null,
    val agentId: String? = null,
    val products: List<PosPair<String?, List<Product>>>? = null,
    val amount: Double? = null,
    val amountPaid: Double? = null,
    val change: Double? = null
){
    val dateString: String get() = DateUtils.getCurrentDateString()
}

data class PosPair<out First,out Second>(
    val first: First? = null,
    val second: Second? = null
)