package com.qr.pos.amper.inventory.ui.add_product

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.qr.pos.amper.auth.domain.interactor.RequiredValidator
import com.qr.pos.amper.inventory.data.dto.Product
import com.qr.pos.amper.inventory.domain.interactor.AddProduct
import com.qr.pos.amper.inventory.domain.interactor.GenerateProductCode
import com.qr.pos.amper.inventory.domain.interactor.UpdateProduct
import com.qr.pos.amper.inventory.ui.edit_product.EditProductEvent
import com.qr.pos.amper.utils.base.event.UiEvent
import com.qr.pos.amper.utils.base.view_model.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddProductViewModel @Inject constructor(
    private val generateProductCode: GenerateProductCode,
    private val createProduct: AddProduct,
    private val requiredValidator: RequiredValidator,
    private val updateProduct: UpdateProduct
): BaseViewModel() {

    private val _uiState = MutableLiveData(AddProductState())
    private val currentState: AddProductState get() = _uiState.value ?: AddProductState()
    val uiState: LiveData<AddProductState> get() = _uiState

    var product = Product(
        visible = true
    )

    fun generateCode(){
        viewModelScope.launch {
            generateProductCode()
                .catch { _uiEvent.value = AddProductEvent.ErrorProductCode }
                .collectLatest {
                    product.productCode = it
                    _uiState.value = currentState.copy(productCode = it)
                }
        }
    }

    fun createProduct(){
        viewModelScope.launch {
            createProduct.invoke(product)
                .onStart { _uiEvent.value = UiEvent.Loading(true) }
                .catch { e ->
                    _uiEvent.value = UiEvent.Loading(false)
                    _uiEvent.value = UiEvent.Error(e.message ?: "")
                }
                .collectLatest {
                    _uiEvent.value = UiEvent.Loading(false)
                    _uiEvent.value = AddProductEvent.ProductCreationSuccess
                }
        }
    }

    fun updateProduct() {
        viewModelScope.launch {
            updateProduct.invoke(product)
                .onStart { _uiEvent.value = UiEvent.Loading(true) }
                .catch { e ->
                    _uiEvent.value = UiEvent.Loading(false)
                    _uiEvent.value = UiEvent.Error(e.message ?: "")
                }
                .collectLatest {
                    _uiEvent.value = UiEvent.Loading(false)
                    _uiEvent.value = EditProductEvent.UpdateSuccess
                }
        }
    }

    fun validatePrice(value: String, field: String) {
        _uiState.value = currentState.copy(
            priceValidation = requiredValidator(value, field)
        )
    }

    fun validateDesc(value: String, field: String) {
        _uiState.value = currentState.copy(
            descValidation = requiredValidator(value, field)
        )
    }

    fun validateCategory(value: String, field: String) {
        _uiState.value = currentState.copy(
            categoryValidation = requiredValidator(value, field)
        )
    }

    fun validateSize(value: String, field: String) {
        _uiState.value = currentState.copy(
            sizeValidation = requiredValidator(value, field)
        )
    }



}