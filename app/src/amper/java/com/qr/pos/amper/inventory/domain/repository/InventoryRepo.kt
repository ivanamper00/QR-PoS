package com.qr.pos.amper.inventory.domain.repository

import com.qr.pos.amper.inventory.data.dto.Product
import com.qr.pos.amper.utils.network.NetResponse
import kotlinx.coroutines.flow.Flow

interface InventoryRepo {

    suspend fun getProducts(): Flow<NetResponse<List<Product>>>

    suspend fun getProduct(productCode: String): Flow<NetResponse<Product>>

    suspend fun getProductLive(productCode: String): Flow<NetResponse<Product>>

    suspend fun addProduct(product: Product): Flow<Unit>

    suspend fun deleteProduct(product: Product): Flow<Unit>

    suspend fun retrieveProduct(product: Product): Flow<Unit>

    suspend fun generateProductCode(): Flow<String>

    suspend fun updateProduct(product: Product): Flow<NetResponse<Product>>

}