package com.qr.pos.amper.utils.base.fragment

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.viewbinding.ViewBinding
import com.qr.pos.amper.R
import com.qr.pos.amper.common.domain.repository.SharedPrefRepo
import com.qr.pos.amper.common.utils.NavigationListener
import com.qr.pos.amper.utils.deligate.UIEventInterface
import javax.inject.Inject

abstract class BaseFragment<Binding: ViewBinding>(layoutRes: Int): Fragment(layoutRes),
    UIEventInterface, NavigationListener {

    private val uiInterface by lazy {
        requireActivity() as UIEventInterface
    }

    private val navigation by lazy {
        requireActivity() as NavigationListener
    }

    @Inject
    lateinit var sf: SharedPrefRepo

    abstract val binding: Binding

    protected val alertDialog : AlertDialog.Builder get() = AlertDialog.Builder(requireContext())

    protected var navController: NavController? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        setupListener()
        setupView()
    }

    abstract fun setupView()

    abstract fun setupListener()

    override fun showErrorScreen(message: String) {
        uiInterface.showErrorScreen(message)
    }

    override fun showLoading(isLoading: Boolean) {
        uiInterface.showLoading(isLoading)
    }

    override fun logoutUser() {
        navigation.logoutUser()
    }
}