package com.qr.pos.amper.auth.ui.registration

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.navigation.fragment.NavHostFragment
import com.qr.pos.amper.R
import com.qr.pos.amper.auth.data.dto.User
import com.qr.pos.amper.utils.base.activity.BaseActivity
import com.qr.pos.amper.utils.base.binding.viewBinding
import com.qr.pos.amper.databinding.ActivityRegisterBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegistrationActivity: BaseActivity<ActivityRegisterBinding>(R.layout.activity_register) {

    override val binding by viewBinding(ActivityRegisterBinding::inflate)

    override fun setupView() {
    }

    override fun setupListeners() {
    }

    companion object {
        fun getStartIntent(context: Context): Intent
        = Intent(context, RegistrationActivity::class.java)
    }

}