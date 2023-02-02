package com.qr.pos.amper.common.ui

import android.content.Context
import android.content.Intent
import com.qr.pos.amper.R
import com.qr.pos.amper.auth.ui.base.BaseSplashActivity
import com.qr.pos.amper.auth.ui.login.LoginActivity
import com.qr.pos.amper.utils.base.activity.BaseActivity
import com.qr.pos.amper.utils.base.binding.viewBinding
import com.qr.pos.amper.databinding.ActivityMainBinding

class MainActivity : BaseSplashActivity<ActivityMainBinding>(R.layout.activity_main) {

    override val binding by viewBinding(ActivityMainBinding::inflate)

    override fun setupListeners() {

    }

    override fun setupView() {
        startActivity(LoginActivity.getStartIntent(this))
        finish()
    }

    companion object {
        fun getStartIntent(context: Context) = Intent(context, MainActivity::class.java)
    }

}