package com.qr.pos.amper.cashier.ui.pos

import com.qr.pos.amper.auth.utils.UserType
import com.qr.pos.amper.utils.base.event.UiEvent

sealed class PosEvent: UiEvent() {
    data class ReviewTransaction(val change: Double): UiEvent()
    data class EndSession(val userType: UserType): UiEvent()
    object VoidSuccess: UiEvent()
}