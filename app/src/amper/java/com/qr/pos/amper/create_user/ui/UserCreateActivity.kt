package com.qr.pos.amper.create_user.ui

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.qr.pos.amper.R
import com.qr.pos.amper.auth.ui.registration.RegistrationActivity
import com.qr.pos.amper.databinding.ActivityUserCreateBinding
import com.qr.pos.amper.utils.base.activity.BaseActivity
import com.qr.pos.amper.utils.base.binding.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserCreateActivity : BaseActivity<ActivityUserCreateBinding>(R.layout.activity_user_create) {

    override val binding: ActivityUserCreateBinding by viewBinding(ActivityUserCreateBinding::inflate)

    override fun setupView() {

    }

    override fun setupListeners() {

    }

    companion object {
        fun getStartIntent(context: Context): Intent = Intent(context, UserCreateActivity::class.java)
    }

}