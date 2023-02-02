package com.qr.pos.amper.inventory.ui.edit_product

import com.qr.pos.amper.utils.base.event.UiEvent

sealed class EditProductEvent: UiEvent() {
    object UpdateSuccess: UiEvent()
}