package com.qr.pos.amper.auth.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.qr.pos.amper.auth.data.dto.User
import com.qr.pos.amper.auth.domain.interactor.*
import com.qr.pos.amper.auth.utils.UserType
import com.qr.pos.amper.common.domain.interactor.GetServerTime
import com.qr.pos.amper.utils.DateUtils
import com.qr.pos.amper.utils.StringUtils
import com.qr.pos.amper.utils.base.event.UiEvent
import com.qr.pos.amper.utils.base.view_model.BaseViewModel
import com.qr.pos.amper.utils.network.NetResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val emailValidator: EmailValidator,
    private val ssoGoogle: SSOGoogle,
    private val requiredValidator: RequiredValidator,
    private val emailLogin: EmailLogin,
    private val getActivationDetails: GetActivationDetails,
    private val getServerTime: GetServerTime,
    private val activateBilling: ActivateBilling
): BaseViewModel() {

    private var _uiState = MutableLiveData(LoginState())
    val uiState: LiveData<LoginState>
        get() = _uiState
    private val loginState: LoginState get() = _uiState.value ?: LoginState()


    fun validateEmail(email: String, field: String) {
        val result = emailValidator(email, field)
        if(!result.passed){
            _uiState.value = loginState.copy(
                validationError = result.message
            )
        }
        _uiState.value = loginState.copy(
            isEmailValid = result.passed,
            email = email
        )
    }

    fun validatePassword(password: String, field: String) {
        val result = requiredValidator(password, field)
        if(!result.passed){
            _uiState.value = loginState.copy(
                validationError = result.message
            )
        }
        _uiState.value = loginState.copy(
            isPasswordValid = result.passed,
            password = password
        )
    }

    fun loginViaGoogle(idToken: String?, user: User) {
        idToken?.let {
            viewModelScope.launch {
                ssoGoogle(it, user)
                    .onStart { _uiEvent.value = UiEvent.Loading(true) }
                    .catch { e ->
                        _uiEvent.value = UiEvent.Loading(false)
                        _uiEvent.value = UiEvent.Error(e.message ?: "")
                    }
                    .collectLatest {
                        _uiEvent.value = UiEvent.Loading(false)
                    }
            }
        }
    }

    fun login() {
        viewModelScope.launch {
            emailLogin(loginState.email ?: "", loginState.password ?: "")
                .onStart { _uiEvent.value = UiEvent.Loading(true) }
                .catch { e ->
                    _uiEvent.value = UiEvent.Loading(false)
                    _uiEvent.value = UiEvent.Error(e.message ?: "")
                }.collectLatest {
                    _uiEvent.value = UiEvent.Loading(false)
                    when(it){
                        is NetResponse.Success -> it.data.user?.userTypeKey?.let { key ->
                            _uiState.value = loginState.copy(
                                userType = UserType.fromTypeKey(key)
                            )
                            _uiEvent.value = LoginEvent.LoginSuccess
                        }
                        else -> {}
                    }
                }
        }
    }

    fun checkBilling() {
        viewModelScope.launch {
            getActivationDetails.invoke(StringUtils.getDomain(loginState.email ?: ""))
                .onStart { _uiEvent.value = UiEvent.Loading(true) }
                .catch { e ->
                    _uiEvent.value = UiEvent.Loading(false)
                    _uiEvent.value = UiEvent.Error(e.message ?: "")
                }.collectLatest {
                    if(it.endDate == null){
                        _uiEvent.value = LoginEvent.BillingRequired
                    }else {
                        val endDate = DateUtils.getConvertedDate(it.endDate)
                        if(endDate <= loginState.serverDate) _uiEvent.value = LoginEvent.BillingRequired
                        else _uiEvent.value = LoginEvent.BillingVerified(loginState.userType!!)
                    }
                    _uiEvent.value = UiEvent.Loading(false)
                }
        }
    }

    fun getServerDate() {
        viewModelScope.launch {
            getServerTime.invoke()
                .onStart { _uiEvent.value = UiEvent.Loading(true) }
                .catch { e ->
                    _uiEvent.value = UiEvent.Loading(false)
                    _uiEvent.value = UiEvent.Error(e.message ?: "")
                }.collectLatest {
                    _uiState.value = loginState.copy(
                        serverDate = DateUtils.getConvertedDate(it.date!!)
                    )
                    _uiEvent.value = UiEvent.Loading(false)
                }
        }
    }

    fun activateBilling(activationCode: String) {
        viewModelScope.launch {
            activateBilling(activationCode, StringUtils.getDomain(loginState.email ?: ""))
                .onStart { _uiEvent.value = UiEvent.Loading(true) }
                .catch { e ->
                    _uiEvent.value = UiEvent.Loading(false)
                    _uiEvent.value = UiEvent.Error(e.message ?: "")
                }.collectLatest {
                    _uiEvent.value = UiEvent.Loading(false)
                    _uiEvent.value = LoginEvent.BillingVerified(loginState.userType!!)
                }
        }
    }
}