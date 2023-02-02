package com.qr.pos.amper.accounting.ui

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.qr.pos.amper.R
import com.qr.pos.amper.cashier.ui.PosActivity
import com.qr.pos.amper.databinding.ActivityAccountingBinding
import com.qr.pos.amper.utils.base.activity.BaseActivity
import com.qr.pos.amper.utils.base.binding.viewBinding

class AccountingActivity : BaseActivity<ActivityAccountingBinding>(R.layout.activity_accounting) {

    override val binding: ActivityAccountingBinding by viewBinding(ActivityAccountingBinding::inflate)

    override fun setupView() {
    }

    override fun setupListeners() {
    }

    companion object {
        fun getStartIntent(context: Context) = Intent(context, AccountingActivity::class.java)
    }
}