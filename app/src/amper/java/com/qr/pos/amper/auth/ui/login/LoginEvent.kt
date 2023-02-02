package com.qr.pos.amper.auth.ui.login

import com.qr.pos.amper.auth.data.dto.User
import com.qr.pos.amper.auth.utils.UserType
import com.qr.pos.amper.utils.base.event.UiEvent

class LoginEvent: UiEvent() {
    data class SSORegistration(val user: User): UiEvent()
    object LoginSuccess: UiEvent()
    object BillingRequired: UiEvent()
    data class BillingVerified(val userType: UserType): UiEvent()
}