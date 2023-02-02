package com.qr.pos.amper.cashier.ui

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import com.qr.pos.amper.R
import com.qr.pos.amper.common.ui.DashboardActivity
import com.qr.pos.amper.databinding.ActivityDashboardBinding
import com.qr.pos.amper.databinding.ActivityPosBinding
import com.qr.pos.amper.utils.base.activity.BaseActivity
import com.qr.pos.amper.utils.base.binding.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PosActivity : BaseActivity<ActivityPosBinding>(R.layout.activity_pos) {

    override val binding: ActivityPosBinding by viewBinding(ActivityPosBinding::inflate)


    override fun setupView() {

    }

    override fun setupListeners() {
    }

    companion object {
        fun getStartIntent(context: Context) = Intent(context, PosActivity::class.java)
    }
}