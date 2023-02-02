package com.qr.pos.amper.auth.ui.registration.fragments

import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputLayout
import com.qr.pos.amper.R
import com.qr.pos.amper.auth.ui.registration.RegistrationViewModel
import com.qr.pos.amper.auth.utils.UserType
import com.qr.pos.amper.utils.base.binding.viewBinding
import com.qr.pos.amper.utils.base.event.UiEvent
import com.qr.pos.amper.utils.base.fragment.BaseFragment
import com.qr.pos.amper.utils.extensions.textChanges
import com.qr.pos.amper.databinding.FragmentEmailRegistrationBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class EmailRegistrationFragment : BaseFragment<FragmentEmailRegistrationBinding>(R.layout.fragment_email_registration),
    AdapterView.OnItemSelectedListener {

    override val binding by viewBinding(FragmentEmailRegistrationBinding::bind)

    private val viewModel by viewModels<RegistrationViewModel>()

    override fun setupListener() {
        viewModel.uiState.observe(this){ state ->
            setSignUpButtonEnabled(state.isButtonSignUpEnabled())
            with(viewModel){
                setError(binding.emailContainer, state.emailValidation?.message)
                setError(binding.firstNameContainer, state.firstNameValidation?.message)
                setError(binding.lastNameContainer, state.lastNameValidation?.message)
                setError(binding.phoneContainer, state.phoneValidation?.message)
                setError(binding.passwordContainer, state.passwordValidation?.message)
                setError(binding.confirmPasswordContainer, state.confirmPassValidation?.message)
            }
        }

        viewModel.uiEvent.observe(this){ event ->
            when(event){
                is UiEvent.Loading -> showLoading(event.isLoading)
                is UiEvent.Error -> showErrorScreen(event.message)
                is UiEvent.Success -> showRegistrationSuccess()
            }
        }
    }

    private fun showRegistrationSuccess() {
        alertDialog.setTitle(getString(R.string.register_dialog_title))
            .setMessage(getString(R.string.register_dialog_message))
            .setPositiveButton(getString(R.string.dialog_ok_label)){ d, _ ->
                d.dismiss()
                requireActivity().finish()
            }.create().show()
    }

    private fun setSignUpButtonEnabled(buttonSignUpEnabled: Boolean) {
        binding.signUpButton.isEnabled = buttonSignUpEnabled
    }

    override fun setupView() {
        with(binding){
            user = viewModel.user

            signUpButton.setOnClickListener {
                viewModel.user.userTypeKey = UserType.ADMIN.key
                viewModel.user.userType = UserType.ADMIN.alias
                viewModel.register()
            }

            firstNameEditText.textChanges()
                .debounce(300)
                .drop(1)
                .onEach {
                    viewModel.validateFirstName(it.toString(), getString(R.string.registration_label_first_name))
                }.launchIn(lifecycleScope)

            lastNameEditText.textChanges()
                .debounce(300)
                .drop(1)
                .onEach {
                    viewModel.validateLastName(it.toString(), getString(R.string.registration_label_last_name))
                }.launchIn(lifecycleScope)

            phoneEditText.textChanges()
                .debounce(300)
                .drop(1)
                .onEach {
                    viewModel.validatePhone(it.toString(), getString(R.string.registration_label_contact))
                }.launchIn(lifecycleScope)

            emailEditText.textChanges()
                .debounce(300)
                .drop(1)
                .onEach {
                    viewModel.validateEmail(it.toString(), getString(R.string.registration_label_email))
                }.launchIn(lifecycleScope)

            passwordEditText.textChanges()
                .debounce(300)
                .drop(1)
                .onEach {
                    viewModel.validatePassword(it.toString(), getString(R.string.login_label_password))
                }.launchIn(lifecycleScope)

            confirmPasswordEditText.textChanges()
                .debounce(300)
                .drop(1)
                .onEach {
                    viewModel.validateConfirmPassword(it.toString(), passwordEditText.text.toString(), getString(R.string.registration_label_confirm_password))
                }.launchIn(lifecycleScope)
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

    }

    override fun onNothingSelected(parent: AdapterView<*>?) {}
}