package com.qr.pos.amper.auth.domain.interactor

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import com.qr.pos.amper.auth.data.dto.User
import com.qr.pos.amper.auth.domain.repository.LoginRepo
import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Inject

@ActivityRetainedScoped
class EmailLogin @Inject constructor(
    private val loginRepo: LoginRepo
) {
    suspend operator fun invoke(email: String, password: String) = loginRepo.login(email, password)
}