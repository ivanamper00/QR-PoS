package com.qr.pos.amper.utils.base.event

open class UiState {
    data class Loading(val isLoading: Boolean): UiState()
}