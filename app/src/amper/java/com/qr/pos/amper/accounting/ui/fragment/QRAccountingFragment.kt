package com.qr.pos.amper.accounting.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.view.PreviewView
import androidx.fragment.app.viewModels
import com.qr.pos.amper.R
import com.qr.pos.amper.accounting.ui.AccountingViewModel
import com.qr.pos.amper.databinding.FragmentQRAccountingBinding
import com.qr.pos.amper.utils.base.binding.viewBinding
import com.qr.pos.amper.utils.base.fragment.BaseScannerFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class QRAccountingFragment : BaseScannerFragment<FragmentQRAccountingBinding>(R.layout.fragment_q_r_accounting) {

    override val binding: FragmentQRAccountingBinding by viewBinding(FragmentQRAccountingBinding::bind)

    override val cameraPreview: PreviewView
        get() = binding.accountingQr

    private val viewModel by viewModels<AccountingViewModel>()

    override fun setupListener() {

    }

    override fun onQRFound(value: String) {

    }


}