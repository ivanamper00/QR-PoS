package com.qr.pos.amper.auth.ui.login

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.qr.pos.amper.R
import com.qr.pos.amper.auth.data.dto.User
import com.qr.pos.amper.auth.ui.registration.RegistrationActivity
import com.qr.pos.amper.auth.utils.UserType
import com.qr.pos.amper.cashier.ui.PosActivity
import com.qr.pos.amper.utils.base.activity.BaseActivity
import com.qr.pos.amper.utils.base.binding.viewBinding
import com.qr.pos.amper.utils.base.event.UiEvent
import com.qr.pos.amper.utils.extensions.textChanges
import com.qr.pos.amper.common.ui.DashboardActivity
import com.qr.pos.amper.databinding.ActivityLoginBinding
import com.qr.pos.amper.inventory.ui.InventoryActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity: BaseActivity<ActivityLoginBinding>(R.layout.activity_login), DialogActivationCode.CodeListener {

    override val binding by viewBinding(ActivityLoginBinding::inflate)

    private val viewModel: LoginViewModel by viewModels()

    private val activationDialog by lazy {
        DialogActivationCode(
            listener = this,
            buttonText = getString(R.string.activate_and_login_label),
            hintText = getString(R.string.activation_code_label),
            title = getString(R.string.domain_expired_label),
            message = getString(R.string.activation_expired_message),
            reminder = getString(R.string.activation_expired_reminder)
        )
    }

    @Inject
    lateinit var googleSignInOptions: GoogleSignInOptions

    private val googleSignInClient by lazy {
        GoogleSignIn.getClient(this, googleSignInOptions)
    }

    private val googleSignInActivityResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
        if(result.resultCode == Activity.RESULT_OK){
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account: GoogleSignInAccount = task.getResult(ApiException::class.java)
                val user = User(
                    firstName = account.givenName,
                    lastName = account.familyName,
                    email = account.email
                )
                viewModel.loginViaGoogle(account.idToken,user)
            } catch (e: ApiException) {
                Toast.makeText(this, "Failed to sign in using google.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun setupListeners() {
        viewModel.uiEvent.observe(this){ event ->
            when(event){
                is UiEvent.Loading -> showLoading(event.isLoading)
                is UiEvent.Error -> showErrorScreen(event.message)
                is LoginEvent.LoginSuccess -> viewModel.checkBilling()
                is LoginEvent.BillingRequired -> showDomainExpired()
                is LoginEvent.BillingVerified -> onBillingVerified(event.userType)
            }
        }

        viewModel.uiState.observe(this){ state->
            binding.loginButton.isEnabled = state.isLoginButtonEnabled
        }

        viewModel.getServerDate()
    }

    private fun showDomainExpired() {
        activationDialog.show(supportFragmentManager, DialogActivationCode::class.java.simpleName)
    }

    private fun onBillingVerified(userType: UserType) {
        when(userType){
            UserType.ADMIN, UserType.HEAD -> startActivity(DashboardActivity.getStartIntent(this))
            UserType.CASHIER -> startActivity(PosActivity.getStartIntent(this))
            UserType.INVENTORY -> startActivity(InventoryActivity.getStartIntent(this))
        }
        finish()
    }

    override fun setupView() {
        with(binding){
            loginButton.setOnClickListener {
                viewModel.login()
            }
            loginGoogle.setOnClickListener {
                googleSignInActivityResult.launch(googleSignInClient.signInIntent)
            }
            username.textChanges()
                .debounce(300)
                .onEach {
                    viewModel.validateEmail(it.toString(), getString(R.string.registration_label_email))
                }.launchIn(lifecycleScope)

            password.textChanges()
                .debounce(300)
                .onEach {
                    viewModel.validatePassword(it.toString(), getString(R.string.login_label_password))
                }.launchIn(lifecycleScope)

            setSpannableText(registrationButton)
        }
    }

    private fun setSpannableText(registrationButton: TextView) {
        val clickableDesc = getString(R.string.Login_label_registration_clickable)
        val desc = getString(R.string.Login_label_registration, clickableDesc)
        val spannableString = SpannableString(desc)
        val clickableSpan = object: ClickableSpan(){
            override fun onClick(p0: View) = navigateToRegistration()
            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
            }
        }
        val start = desc.lastIndexOf(clickableDesc)
        val end = start + clickableDesc.length
        spannableString.setSpan(clickableSpan, start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        registrationButton.text = spannableString
        registrationButton.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun navigateToRegistration() {
        startActivity(RegistrationActivity.getStartIntent(this))
    }

    companion object {
        fun getStartIntent(context: Context): Intent = Intent(context, LoginActivity::class.java)
    }

    override fun onCodeSubmit(activationCode: String) {
        viewModel.activateBilling(activationCode)
    }

    override fun onCancel() {
        logout.invoke()
        with(binding){
            username.text?.clear()
            password.text?.clear()
        }
    }

}