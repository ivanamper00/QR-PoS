package com.qr.pos.amper.cashier.domain.interactor

import com.qr.pos.amper.cashier.data.dto.POSTransaction
import com.qr.pos.amper.cashier.domain.repository.CashierRepo
import javax.inject.Inject

class SaveTransaction @Inject constructor(
    private val cashierRepo: CashierRepo
) {
    suspend operator fun invoke(posTransaction: POSTransaction) = cashierRepo.saveTransaction(posTransaction)
}