package com.qr.pos.amper.utils.base.fragment

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.qr.pos.amper.R

abstract class BaseBottomSheetFragment<Binding: ViewBinding>(layoutRes: Int): BottomSheetDialogFragment(layoutRes) {

    abstract val binding: Binding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListener()
        setupView()
    }

    abstract fun setupListener()

    abstract fun setupView()

}