package com.qr.pos.amper.auth.ui.base

import android.util.Log
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.auth.FirebaseAuth
import com.qr.pos.amper.utils.base.activity.BaseActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch
import javax.inject.Inject

abstract class BaseSplashActivity<Binding: ViewBinding>(layoutRes: Int): BaseActivity<Binding>(layoutRes) {

    @Inject
    lateinit var auth: FirebaseAuth

    override fun onStart() {
        super.onStart()
        lifecycleScope.launch {
            callbackFlow {
                try{
                    auth.addAuthStateListener {
                        trySend(!it.currentUser?.uid.isNullOrEmpty())
                    }
                    awaitClose()
                }catch (e: Exception){
                    close(e)
                }
            }.collectLatest {
                if(it) {
                    loginUser()
                }
            }
        }
    }

    private fun loginUser() {
        Log.d("loginUser", "loginUser")
    }
}