package com.qr.pos.amper.cashier.domain.repository

import com.qr.pos.amper.cashier.data.dto.POSTransaction
import kotlinx.coroutines.flow.Flow

interface CashierRepo {

    suspend fun saveTransaction(posTransaction: POSTransaction): Flow<Unit>

    suspend fun endCashierTransaction(): Flow<Unit>

    suspend fun verifyVoidCode(code: String): Flow<Unit>
}