package com.qr.pos.amper.auth.domain.interactor

import com.qr.pos.amper.auth.domain.repository.LoginRepo
import javax.inject.Inject

class Logout @Inject constructor(
    private val loginRepo: LoginRepo
) {
    operator fun invoke() =  loginRepo.logout()
}