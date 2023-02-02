package com.qr.pos.amper.inventory.domain.interactor

import com.qr.pos.amper.inventory.domain.repository.InventoryRepo
import javax.inject.Inject

class GetProducts @Inject constructor(
    private val inventoryRepo: InventoryRepo
) {
    suspend operator fun invoke() = inventoryRepo.getProducts()
}