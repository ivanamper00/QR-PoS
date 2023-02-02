package com.qr.pos.amper.inventory.ui

import android.content.Context
import android.content.Intent
import androidx.activity.OnBackPressedCallback
import com.qr.pos.amper.R
import com.qr.pos.amper.databinding.ActivityInventoryBinding
import com.qr.pos.amper.utils.base.activity.BaseActivity
import com.qr.pos.amper.utils.base.binding.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InventoryActivity : BaseActivity<ActivityInventoryBinding>(R.layout.activity_inventory) {

    override val binding: ActivityInventoryBinding by viewBinding(ActivityInventoryBinding::inflate)

    override fun setupView() {
    }

    override fun setupListeners() {
    }

    companion object {
        fun getStartIntent(context: Context) = Intent(context, InventoryActivity::class.java)
    }

}