package com.qr.pos.amper.inventory.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.qr.pos.amper.common.utils.FirestoreConstants
import com.qr.pos.amper.common.data.repository.SharedPrefRepoImp
import com.qr.pos.amper.common.domain.repository.SharedPrefRepo
import com.qr.pos.amper.inventory.data.dto.Product
import com.qr.pos.amper.inventory.domain.repository.InventoryRepo
import com.qr.pos.amper.utils.extensions.serializeToMap
import com.qr.pos.amper.utils.network.NetResponse
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class InventoryRepoImp(
    private val db: FirebaseFirestore,
    private val sf: SharedPrefRepo
) : InventoryRepo {
    override suspend fun getProducts(): Flow<NetResponse<List<Product>>> = callbackFlow {
        try{
            val listener = db.collection(FirestoreConstants.FIRESTORE_DOMAIN_COLLECTION)
                .document(sf.getDomain())
                .collection(FirestoreConstants.FIRESTORE_PRODUCT_COLLECTIONS)
                .whereNotEqualTo("visible", false)
                .addSnapshotListener { value, error ->
                    if(error == null){
                        val result = value?.toObjects(Product::class.java)
                        if(result.isNullOrEmpty()) trySend(NetResponse.NullOrEmpty)
                        else trySend(NetResponse.Success(result))
                    }else close(error)
                }
            awaitClose{ listener.remove() }
        }catch (e: Exception){
            close(e)
        }
    }

    override suspend fun getProduct(productCode: String): Flow<NetResponse<Product>> = callbackFlow {
        try {
            db.collection(FirestoreConstants.FIRESTORE_DOMAIN_COLLECTION)
                .document(sf.getDomain())
                .collection(FirestoreConstants.FIRESTORE_PRODUCT_COLLECTIONS)
                .document(productCode)
                .get().addOnCompleteListener {
                    if(it.isSuccessful){
                        val result = it.result?.toObject(Product::class.java)
                        if(result == null) trySend(NetResponse.NullOrEmpty)
                        else if(result.visible == false) trySend(NetResponse.NullOrEmpty)
                        else trySend(NetResponse.Success(result))
                    }else close(it.exception)
                }
            awaitClose()
        }catch (e: Exception){
            close(e)
        }
    }

    override suspend fun getProductLive(productCode: String): Flow<NetResponse<Product>> = callbackFlow {
        try {
            db.collection(FirestoreConstants.FIRESTORE_DOMAIN_COLLECTION)
                .document(sf.getDomain())
                .collection(FirestoreConstants.FIRESTORE_PRODUCT_COLLECTIONS)
                .document(productCode)
                .addSnapshotListener { value, error ->
                    if(error == null){
                        val result = value?.toObject(Product::class.java)
                        if(result == null) trySend(NetResponse.NullOrEmpty)
                        else if(result.visible == false) trySend(NetResponse.NullOrEmpty)
                        else trySend(NetResponse.Success(result))
                    }else close(error)
                }
            awaitClose()
        }catch (e: Exception){
            close(e)
        }
    }

    override suspend fun addProduct(product: Product): Flow<Unit> = callbackFlow {
        try{
            db.collection(FirestoreConstants.FIRESTORE_DOMAIN_COLLECTION)
                .document(sf.getDomain())
                .collection(FirestoreConstants.FIRESTORE_PRODUCT_COLLECTIONS)
                .document(product.productCode ?: "")
                .set(product)
                .addOnCompleteListener {
                    if(it.isSuccessful){
                        trySend(Unit)
                    }else close(it.exception)
                }
            awaitClose()
        }catch (e: Exception){ close(e) }
    }

    override suspend fun deleteProduct(product: Product): Flow<Unit> = callbackFlow {
        try {
            db.collection(FirestoreConstants.FIRESTORE_DOMAIN_COLLECTION)
                .document(sf.getDomain())
                .collection(FirestoreConstants.FIRESTORE_PRODUCT_COLLECTIONS)
                .document(product.productCode ?: "")
                .update(product.copy(visible = false).serializeToMap())
                .addOnCompleteListener {
                    if(it.isSuccessful){
                        trySend(Unit)
                    }else close(it.exception)
                }
            awaitClose()
        }catch (e: Exception) { close(e) }
    }

    override suspend fun retrieveProduct(product: Product): Flow<Unit> = callbackFlow {
        try {
            db.collection(FirestoreConstants.FIRESTORE_DOMAIN_COLLECTION)
                .document(sf.getDomain())
                .collection(FirestoreConstants.FIRESTORE_PRODUCT_COLLECTIONS)
                .document(product.productCode ?: "")
                .update(product.copy(visible = true).serializeToMap())
                .addOnCompleteListener {
                    if(it.isSuccessful){
                        trySend(Unit)
                    }else close(it.exception)
                }
            awaitClose()
        }catch (e: Exception) { close(e) }
    }

    override suspend fun generateProductCode(): Flow<String> = callbackFlow {
        try {
            val id = db.collection(FirestoreConstants.FIRESTORE_DOMAIN_COLLECTION)
                .document(sf.getDomain())
                .collection(FirestoreConstants.FIRESTORE_PRODUCT_COLLECTIONS)
                .document().id
            trySend(id)
            awaitClose()
        }catch (e:Exception) {
            close(e)
        }
    }

    override suspend fun updateProduct(product: Product): Flow<NetResponse<Product>> = callbackFlow {
        try {
            db.collection(FirestoreConstants.FIRESTORE_DOMAIN_COLLECTION)
                .document(sf.getDomain())
                .collection(FirestoreConstants.FIRESTORE_PRODUCT_COLLECTIONS)
                .document(product.productCode ?: "")
                .update(product.serializeToMap())
                .addOnCompleteListener {
                    if(it.isSuccessful){
                        trySend(NetResponse.Success(product))
                    }else close(it.exception)
                }
            awaitClose()
        }catch (e: Exception) {
            close(e)
        }
    }
}