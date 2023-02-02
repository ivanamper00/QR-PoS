package com.qr.pos.amper.cashier.ui.pos.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.qr.pos.amper.inventory.data.dto.Product
import com.qr.pos.amper.inventory.domain.interactor.GetProducts
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
class ProductSearchViewModel @Inject constructor(
    private val getProducts: GetProducts
): BaseViewModel() {

    private val _uiState = MutableLiveData(listOf<Product>())
    val uiState: LiveData<List<Product>> get() = _uiState

    fun getProducts(){
        viewModelScope.launch {
            getProducts.invoke()
                .onStart { _uiEvent.value = UiEvent.Loading(true) }
                .catch { e ->
                    _uiEvent.value = UiEvent.Loading(false)
                    _uiEvent.value = UiEvent.Error(e.message ?: "")
                }
                .collectLatest {
                    when(it){
                        is NetResponse.Success -> _uiState.value = it.data
                        else -> {}
                    }
                    _uiEvent.value = UiEvent.Loading(false)
                }
        }
    }
}