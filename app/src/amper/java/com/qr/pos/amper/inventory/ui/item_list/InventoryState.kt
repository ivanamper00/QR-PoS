package com.qr.pos.amper.inventory.ui.item_list

import com.qr.pos.amper.inventory.data.dto.Product

data class InventoryState(
    val products: List<Product>? = null,
    val currentDeletedProduct: Product? = null
)