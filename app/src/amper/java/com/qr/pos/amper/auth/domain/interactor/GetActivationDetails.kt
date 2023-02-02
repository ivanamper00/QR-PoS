package com.qr.pos.amper.auth.domain.interactor

import com.qr.pos.amper.auth.domain.repository.LoginRepo
import javax.inject.Inject

class GetActivationDetails @Inject constructor(
    private val repo: LoginRepo
) {
    suspend operator fun invoke(domain: String) = repo.getActivationDuration(domain)
}