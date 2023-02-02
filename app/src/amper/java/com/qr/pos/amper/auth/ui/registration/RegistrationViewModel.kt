package com.qr.pos.amper.auth.ui.registration

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import com.qr.pos.amper.auth.data.dto.User
import com.qr.pos.amper.auth.domain.interactor.*
import com.qr.pos.amper.auth.ui.registration.fragments.RegistrationState
import com.qr.pos.amper.utils.base.event.UiEvent
import com.qr.pos.amper.utils.base.view_model.BaseViewModel
import com.qr.pos.amper.utils.network.NetResponse
import com.qr.pos.amper.utils.validation.FieldValidation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val requiredValidator: RequiredValidator,
    private val phoneValidator: PhoneValidator,
    private val emailValidator: EmailValidator,
    private val passwordValidator: PasswordValidator,
    private val confirmPassValidator: ConfirmPassValidator,
    private val emailRemoteValidator: EmailRemoteValidator,
    private val registerUser: RegisterUser
): BaseViewModel() {

    private var _uiState = MutableLiveData(RegistrationState())
    val uiState: LiveData<RegistrationState> get() = _uiState
    private val regState: RegistrationState get() = _uiState.value ?: RegistrationState()

    val user: User = User()

    fun validateFirstName(firstName: String, field: String) {
        _uiState.value = regState.copy(
            firstNameValidation = requiredValidator(firstName,field),
        )
    }

    fun validateLastName(lastName: String, field: String) {
        _uiState.value = regState.copy(
            lastNameValidation = requiredValidator(lastName, field)
        )
    }

    fun validatePhone(phone: String, field: String) {
        _uiState.value = regState.copy(
            phoneValidation = phoneValidator(phone, field)
        )
    }

    fun validateEmail(email: String, field: String) {
        viewModelScope.launch {
            emailRemoteValidator(email)
                .catch { e ->
                    _uiState.value = regState.copy(
                        emailValidation = FieldValidation(false, e.message ?: "")
                    )
                }
                .collectLatest {
                    when(it){
                        is NetResponse.Success -> {
                            if(it.data){
                                _uiState.value = regState.copy(
                                    emailValidation = emailValidator(email, field)
                                )
                            }else {
                                _uiState.value = regState.copy(
                                    emailValidation = FieldValidation(false, "Email exist!")
                                )
                            }
                        }
                        else -> {}
                    }
                }
        }
    }

    fun validatePassword(password: String, field: String) {
        _uiState.value = regState.copy(
            passwordValidation = passwordValidator(password, field),
            password = password
        )
    }

    fun validateConfirmPassword(confirm: String, password: String, field: String) {
        _uiState.value = regState.copy(
            confirmPassValidation = confirmPassValidator(confirm,password,field)
        )
    }

    fun register() {
        _uiState.value = regState.copy(user =  user)
        viewModelScope.launch {
            regState.user?.let {
                Log.d("register_user", Gson().toJson(it))
                registerUser(it, regState.password ?: "")
                    .onStart { _uiEvent.value = UiEvent.Loading(true) }
                    .catch { e ->
                        _uiEvent.value = UiEvent.Error(e.message ?: "")
                        _uiEvent.value = UiEvent.Loading(false)
                    }.collectLatest { result ->
                        when(result){
                            is NetResponse.Success -> _uiEvent.value = UiEvent.Success
                            else -> {}
                        }
                        _uiEvent.value = UiEvent.Loading(false)
                    }
            }
        }
    }

    fun setError(container: TextInputLayout, message: String?) {
        container.error = message
    }

}