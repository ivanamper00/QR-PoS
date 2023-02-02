package com.qr.pos.amper.cashier.ui.pos

import com.qr.pos.amper.cashier.data.dto.PosPair
import com.qr.pos.amper.inventory.data.dto.Product
import com.qr.pos.amper.utils.validation.FieldValidation

data class PosState(
    val products: List<PosPair<String?, List<Product>>> = emptyList(),
    val amountValidation: FieldValidation? = null,
    val amountPaid: Double? = null,
    val amount: Double? = null
){
    fun isVoidButtonEnabled() = products.isNotEmpty()
    fun isPaymentButtonEnabled() = products.isNotEmpty() && amountValidation?.passed == true
    fun getChange(): Double = (amountPaid ?: 0.00) - (amount ?: 0.00)
}