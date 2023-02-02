package com.qr.pos.amper.utils.deligate

interface UIEventInterface {
    fun showErrorScreen(message: String)
    fun showLoading(isLoading: Boolean)
}