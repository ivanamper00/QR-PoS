package com.qr.pos.amper.inventory.domain.interactor

import com.qr.pos.amper.inventory.data.dto.Product
import com.qr.pos.amper.inventory.domain.repository.InventoryRepo
import javax.inject.Inject

class AddProduct @Inject constructor(
    private val inventoryRepo: InventoryRepo
) {
    suspend operator fun invoke(product: Product) = inventoryRepo.addProduct(product)
}