package com.qr.pos.amper.auth.domain.interactor

import com.qr.pos.amper.auth.domain.repository.LoginRepo
import javax.inject.Inject

class ActivateBilling @Inject constructor(
    private val repo: LoginRepo
) {
    suspend operator fun invoke(activationCode: String, domain: String) = repo.activateBilling(activationCode, domain)
}