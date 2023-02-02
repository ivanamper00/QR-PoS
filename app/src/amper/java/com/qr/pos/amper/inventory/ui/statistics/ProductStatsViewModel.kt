package com.qr.pos.amper.inventory.ui.statistics

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.qr.pos.amper.inventory.data.dto.Product
import com.qr.pos.amper.inventory.domain.interactor.GetProduct
import com.qr.pos.amper.inventory.domain.interactor.GetProductLive
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
class ProductStatsViewModel @Inject constructor(
    private val getProduct: GetProductLive
): BaseViewModel() {

    private val _uiState = MutableLiveData(Product())
    val uiState: LiveData<Product> get() = _uiState

    fun getProductLive(productCode: String) {
        viewModelScope.launch {
            getProduct(productCode)
                .onStart { _uiEvent.value = UiEvent.Loading(true) }
                .catch { e ->
                    _uiEvent.value = UiEvent.Loading(false)
                    _uiEvent.value = UiEvent.Error(e.message ?: "")
                }
                .collectLatest {
                    _uiEvent.value = UiEvent.Loading(false)
                    when(it){
                        is NetResponse.Success -> _uiState.value = it.data
                        is NetResponse.NullOrEmpty -> _uiEvent.value = UiEvent.NullOrEmpty
                        else -> {}
                    }
                }
        }
    }
}