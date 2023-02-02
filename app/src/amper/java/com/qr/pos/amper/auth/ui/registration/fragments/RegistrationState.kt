package com.qr.pos.amper.auth.ui.registration.fragments

import com.qr.pos.amper.auth.data.dto.User
import com.qr.pos.amper.utils.validation.FieldValidation

data class RegistrationState(
    var firstNameValidation: FieldValidation? = null,
    var lastNameValidation: FieldValidation? = null,
    var phoneValidation: FieldValidation? = null,
    var passwordValidation: FieldValidation? = null,
    var emailValidation: FieldValidation? = null,
    var confirmPassValidation: FieldValidation? = null,
    var user: User? = null,
    var password: String? = null
){
    fun isButtonSignUpEnabled(): Boolean = firstNameValidation?.passed == true &&
            lastNameValidation?.passed == true &&
            phoneValidation?.passed == true &&
            emailValidation?.passed == true &&
            passwordValidation?.passed == true &&
            confirmPassValidation?.passed == true
}
