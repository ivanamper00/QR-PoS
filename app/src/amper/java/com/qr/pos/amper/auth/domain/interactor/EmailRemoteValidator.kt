package com.qr.pos.amper.auth.domain.interactor

import com.qr.pos.amper.auth.data.remote.RegisterRepoImp
import com.qr.pos.amper.auth.domain.repository.RegistrationRepo
import javax.inject.Inject

class EmailRemoteValidator @Inject constructor(
    val registerRepoImp: RegistrationRepo
) {
    suspend operator fun invoke(email: String) = registerRepoImp.validateEmail(email)
}