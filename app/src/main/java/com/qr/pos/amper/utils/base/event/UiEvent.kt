package com.qr.pos.amper.utils.base.event

open class UiEvent {
    object Success: UiEvent()
    data class Error(val message: String): UiEvent()
    data class Loading(val isLoading: Boolean): UiEvent()
    object NullOrEmpty: UiEvent()
}
