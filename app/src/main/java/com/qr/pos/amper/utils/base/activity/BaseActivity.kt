package com.qr.pos.amper.utils.base.activity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.qr.pos.amper.R
import com.qr.pos.amper.auth.domain.interactor.Logout
import com.qr.pos.amper.auth.ui.login.LoginActivity
import com.qr.pos.amper.common.utils.NavigationListener
import com.qr.pos.amper.utils.deligate.UIEventInterface
import javax.inject.Inject

abstract class BaseActivity<Binding: ViewBinding>(private val layoutRes: Int): AppCompatActivity(), UIEventInterface,
    NavigationListener {

    abstract val binding: Binding

    protected val alertDialog : AlertDialog.Builder get() = AlertDialog.Builder(this)

    @Inject
    lateinit var logout: Logout

    private val loadingDialog by lazy {
        alertDialog.setView(R.layout.custom_loading)
            .setCancelable(false)
            .create()
    }

    abstract fun setupView()

    abstract fun setupListeners()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(this::class.java.simpleName, "onCreate")
        setContentView(layoutRes)
        logCurrentActivity()
        setupView()
        setupListeners()
    }

    private fun logCurrentActivity() {
        Log.d("CURRENT_SCREEN", this::class.java.simpleName)
    }

    override fun onDestroy() {
        super.onDestroy()
        loadingDialog.dismiss()
        Log.d(this::class.java.simpleName, "onDestroy")
    }

    override fun onStart() {
        super.onStart()
        Log.d(this::class.java.simpleName, "onStart")
    }

    override fun showErrorScreen(message: String){
        alertDialog.setTitle(getString(R.string.dialog_error_title))
            .setMessage(message)
            .setPositiveButton(getString(R.string.dialog_ok_label)){ d, _ ->
                d.dismiss()
            }.create().show()
    }

    override fun showLoading(isLoading: Boolean){
        if(isLoading) loadingDialog.show()
        else loadingDialog.hide()
    }

    override fun logoutUser() {
        logout.invoke()
        startActivity(LoginActivity.getStartIntent(this))
        finish()
    }

}