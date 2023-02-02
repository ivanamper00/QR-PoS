package com.qr.pos.amper.inventory.ui.add_product

import com.qr.pos.amper.utils.base.event.UiEvent

sealed class AddProductEvent: UiEvent() {
    object ErrorProductCode: UiEvent()
    object ProductCreationSuccess : UiEvent()
}