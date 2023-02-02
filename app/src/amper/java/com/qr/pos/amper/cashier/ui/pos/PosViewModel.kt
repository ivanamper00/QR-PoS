package com.qr.pos.amper.cashier.ui.pos

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.qr.pos.amper.auth.domain.interactor.Logout
import com.qr.pos.amper.auth.domain.interactor.RequiredValidator
import com.qr.pos.amper.auth.ui.login.LoginEvent
import com.qr.pos.amper.auth.utils.UserType
import com.qr.pos.amper.cashier.data.dto.POSTransaction
import com.qr.pos.amper.cashier.data.dto.PosPair
import com.qr.pos.amper.cashier.domain.interactor.EndCashierSession
import com.qr.pos.amper.cashier.domain.interactor.PaymentValidator
import com.qr.pos.amper.cashier.domain.interactor.SaveTransaction
import com.qr.pos.amper.cashier.domain.interactor.VerifyVoidCode
import com.qr.pos.amper.common.domain.repository.SharedPrefRepo
import com.qr.pos.amper.inventory.data.dto.Product
import com.qr.pos.amper.inventory.domain.interactor.GetProduct
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
class PosViewModel @Inject constructor(
    private val getProduct: GetProduct,
    private val saveTransaction: SaveTransaction,
    private val endSession: EndCashierSession,
    private val shareRepo: SharedPrefRepo,
    private val paymentValidator: PaymentValidator,
    private val verifyVoidCode: VerifyVoidCode
) : BaseViewModel(){


    private val _uiState = MutableLiveData(PosState())
    private val posState: PosState get() = _uiState.value ?: PosState()
    val uiState: LiveData<PosState> get() = _uiState

    private val products = mutableListOf<Product>()

    fun getProduct(value: String) {
       viewModelScope.launch {
           getProduct.invoke(value)
               .onStart { _uiEvent.value = UiEvent.Loading(true) }
               .catch { e ->
                   _uiEvent.value = UiEvent.Loading(false)
                   _uiEvent.value = UiEvent.Error(e.message ?: "")
               }
               .collectLatest {
                   _uiEvent.value = UiEvent.Loading(false)
                   when(it){
                       is NetResponse.Success -> addProduct(it.data)
                       is NetResponse.NullOrEmpty -> _uiEvent.value = UiEvent.NullOrEmpty
                       else -> {}
                   }
               }
       }
    }

    fun addProduct(product: Product){
        products.add(product)
        _uiState.value = posState.copy(
            products = products.groupBy { p -> p.productCode }.map { mp -> PosPair(mp.key, mp.value) }.toList(),
            amount =  products.sumOf { i -> i.price ?: 0.00 }
        )
    }

    fun validateAmount(value: String, field: String) {
        _uiState.value = posState.copy(
            amountValidation =  paymentValidator(value, posState.amount ?: 0.00, field),
            amountPaid = value.toDoubleOrNull()
        )
    }

    fun calculate() {
        _uiEvent.value = PosEvent.ReviewTransaction(posState.getChange())
    }

    fun saveTransaction() {
        val posTransaction = POSTransaction(
            products = posState.products,
            amount = posState.amount,
            amountPaid = posState.amountPaid,
            change = posState.getChange()
        )
        viewModelScope.launch {
            saveTransaction(posTransaction)
                .onStart { _uiEvent.value = UiEvent.Loading(true) }
                .catch { e ->
                    _uiEvent.value = UiEvent.Loading(false)
                    _uiEvent.value = UiEvent.Error(e.message ?: "")
                }
                .collectLatest {
                    _uiEvent.value = UiEvent.Loading(false)
                    _uiEvent.value = UiEvent.Success
                }
        }
    }

    fun newTransaction() {
        products.clear()
        _uiState.value = PosState()
    }

    fun endCashierSession() {
        viewModelScope.launch {
            endSession()
                .onStart { _uiEvent.value = UiEvent.Loading(true) }
                .catch { e ->
                    _uiEvent.value = UiEvent.Loading(false)
                    _uiEvent.value = UiEvent.Error(e.message ?: "")
                }.collectLatest {
                    _uiEvent.value = UiEvent.Loading(false)
                    shareRepo.getUser().userTypeKey?.let {
                        _uiEvent.value = PosEvent.EndSession(UserType.fromTypeKey(it))
                    }
                }
        }
    }

    fun isSessionEmpty() = products.isEmpty()

    fun verifyVoidCode(activationCode: String) {
        viewModelScope.launch {
            verifyVoidCode.invoke(activationCode)
                .onStart { _uiEvent.value = UiEvent.Loading(true) }
                .catch { e ->
                    _uiEvent.value = UiEvent.Loading(false)
                    _uiEvent.value = UiEvent.Error(e.message ?: "")
                }.collectLatest {
                    _uiEvent.value = UiEvent.Loading(false)
                    _uiEvent.value = PosEvent.VoidSuccess
                }
        }
    }
}