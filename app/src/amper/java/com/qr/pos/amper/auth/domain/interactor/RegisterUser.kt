package com.qr.pos.amper.auth.domain.interactor

import com.qr.pos.amper.auth.data.dto.User
import com.qr.pos.amper.auth.domain.repository.RegistrationRepo
import javax.inject.Inject

class RegisterUser @Inject constructor(
    private val registerRepo: RegistrationRepo
) {
    suspend operator fun invoke(user: User, password: String) = registerRepo.registerUser(user,password)
}