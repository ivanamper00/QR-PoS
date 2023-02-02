package com.qr.pos.amper.auth.ui.login

import com.qr.pos.amper.auth.utils.UserType
import com.qr.pos.amper.utils.base.event.UiState
import java.util.Date

data class LoginState(
    var isEmailValid: Boolean? = false,
    var validationError: String? = null,
    var isPasswordValid: Boolean? = false,
    var email: String? = null,
    var password: String? = null,
    var serverDate: Date? = null,
    var userType: UserType? = null
) {
    val isLoginButtonEnabled: Boolean
    get() = isEmailValid == true && isPasswordValid == true
}