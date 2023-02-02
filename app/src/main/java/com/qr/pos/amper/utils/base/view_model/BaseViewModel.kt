package com.qr.pos.amper.utils.base.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.qr.pos.amper.utils.base.event.UiEvent
import com.qr.pos.amper.utils.base.event.UiState
import dagger.hilt.android.lifecycle.HiltViewModel

abstract class BaseViewModel: ViewModel() {

    protected var _uiEvent = MutableLiveData<UiEvent>()
    val uiEvent: LiveData<UiEvent>
        get() = _uiEvent

    override fun onCleared() {
        super.onCleared()
    }
}