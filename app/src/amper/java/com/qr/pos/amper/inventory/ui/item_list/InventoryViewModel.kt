package com.qr.pos.amper.inventory.ui.item_list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.qr.pos.amper.inventory.data.dto.Product
import com.qr.pos.amper.inventory.domain.interactor.*
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
class InventoryViewModel @Inject constructor(
    private val getProducts: GetProducts,
    private val deleteProduct: DeleteProduct,
    private val getProduct: GetProduct,
    private val undoDelete: RetrieveProduct
): BaseViewModel() {

    private val _uiState = MutableLiveData(InventoryState())
    private val inventoryState: InventoryState get() = _uiState.value ?: InventoryState()
    val uiState: LiveData<InventoryState> get() = _uiState

    fun getProductList(){
        viewModelScope.launch {
            getProducts()
                .onStart { _uiEvent.value = UiEvent.Loading(true) }
                .catch {
                    _uiEvent.value = UiEvent.Loading(false)
                    _uiEvent.value = UiEvent.Error(it.message ?: "")
                }
                .collectLatest {
                    _uiEvent.value = UiEvent.Loading(false)
                    when(it){
                        is NetResponse.Success -> _uiState.value = inventoryState.copy(
                            products = it.data
                        )
                        else -> {}
                    }
                }
        }
    }

    fun deleteProduct(product: Product){
        viewModelScope.launch {
            deleteProduct.invoke(product)
                .onStart { _uiEvent.value = UiEvent.Loading(true) }
                .catch {
                    _uiEvent.value = UiEvent.Loading(false)
                    _uiEvent.value = UiEvent.Error(it.message ?: "")
                }.collectLatest {
                    _uiEvent.value = UiEvent.Loading(false)
                    _uiEvent.value = InventoryEvent.ProductDeleted(product)
                    _uiState.value = inventoryState.copy(
                        currentDeletedProduct = product
                    )
                }
        }
    }

//    fun getProductItem(productCode: String){
//        viewModelScope.launch {
//            getProduct(productCode)
//                .onStart { _uiEvent.value = UiEvent.Loading(true) }
//                .catch {
//                    _uiEvent.value = UiEvent.Loading(false)
//                    _uiEvent.value = UiEvent.Error(it.message ?: "")
//                }.collectLatest {
//                    _uiEvent.value = UiEvent.Loading(false)
//                    when(it){
//                        is NetResponse.Success -> _uiEvent.value = InventoryEvent.EditProduct(it.data)
//                        is NetResponse.NullOrEmpty -> _uiEvent.value = InventoryEvent.ProductNotFound
//                        else -> {}
//                    }
//                }
//        }
//    }

    fun undoDelete(product: Product){
        viewModelScope.launch {
            undoDelete.invoke(product)
                .onStart { _uiEvent.value = UiEvent.Loading(true) }
                .catch {
                    _uiEvent.value = UiEvent.Loading(false)
                    _uiEvent.value = UiEvent.Error(it.message ?: "")
                }.collectLatest {
                    _uiEvent.value = UiEvent.Loading(false)
                    _uiEvent.value = InventoryEvent.UndoDeleteSuccess(product)
                }
        }
    }

}