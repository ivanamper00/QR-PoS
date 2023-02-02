package com.qr.pos.amper.cashier.domain.interactor

import com.qr.pos.amper.cashier.data.dto.POSTransaction
import com.qr.pos.amper.cashier.domain.repository.CashierRepo
import javax.inject.Inject

class VerifyVoidCode @Inject constructor(
    private val cashierRepo: CashierRepo
) {
    suspend operator fun invoke(code: String) = cashierRepo.verifyVoidCode(code)
}