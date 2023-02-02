package com.qr.pos.amper.inventory.ui.item_list

import com.qr.pos.amper.inventory.data.dto.Product
import com.qr.pos.amper.utils.base.event.UiEvent

sealed class InventoryEvent: UiEvent() {
    data class ProductDeleted(val data: Product): UiEvent()
    data class UndoDeleteSuccess(val data: Product): UiEvent()
}