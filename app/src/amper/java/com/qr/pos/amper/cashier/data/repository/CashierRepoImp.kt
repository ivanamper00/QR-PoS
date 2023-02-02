package com.qr.pos.amper.cashier.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.gson.Gson
import com.qr.pos.amper.auth.data.dto.ActivationModel
import com.qr.pos.amper.cashier.data.dto.POSTransaction
import com.qr.pos.amper.cashier.data.dto.PosPair
import com.qr.pos.amper.cashier.domain.repository.CashierRepo
import com.qr.pos.amper.common.domain.repository.SharedPrefRepo
import com.qr.pos.amper.common.utils.FirestoreConstants
import com.qr.pos.amper.inventory.data.dto.Product
import com.qr.pos.amper.utils.DateUtils
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.*
import javax.inject.Inject

class CashierRepoImp @Inject constructor(
    private val db: FirebaseFirestore,
    private val sf: SharedPrefRepo
): CashierRepo {

    override suspend fun saveTransaction(posTransaction: POSTransaction): Flow<Unit> = callbackFlow {
        try {
            val agent = sf.getUser()
           val id = db.collection(FirestoreConstants.FIRESTORE_DOMAIN_COLLECTION)
                .document(sf.getDomain())
                .collection(FirestoreConstants.FIRESTORE_TRANSACTIONS_COLLECTION)
                .document().id

            db.collection(FirestoreConstants.FIRESTORE_DOMAIN_COLLECTION)
                .document(sf.getDomain())
                .collection(FirestoreConstants.FIRESTORE_TRANSACTIONS_COLLECTION)
                .document(id).set(
                    posTransaction.copy(
                        id = id,
                        agentId = agent.id,
                        agent = "${agent.firstName} ${agent.lastName}"
                    )
                )
                .addOnCompleteListener {
                    if(it.isSuccessful) trySend(Unit)
                    else close(it.exception)
                }
            awaitClose()
        }catch (e: Exception){
            close(e)
        }
    }

    override suspend fun endCashierTransaction(): Flow<Unit> = callbackFlow {
        try {
            val agent = sf.getUser()
            db.collection(FirestoreConstants.FIRESTORE_DOMAIN_COLLECTION)
                .document(sf.getDomain())
                .collection(FirestoreConstants.FIRESTORE_TRANSACTIONS_COLLECTION)
                .whereEqualTo("agentId", agent.id)
                .whereEqualTo("dateString", DateUtils.getCurrentDateString())
                .get()
                .addOnCompleteListener {
                    if(it.isSuccessful){
                        val result = it.result.toObjects(POSTransaction::class.java)
                        Log.d("endCashierTransaction",  Gson().toJson(result))
                        val productList = mutableListOf<PosPair<String?, List<Product>>>()
                        result.forEach { posTrans ->
                            posTrans.products?.let { prods ->  productList.addAll(prods) }
                        }
                        val transaction = POSTransaction(
                            id = "${DateUtils.getCurrentDateIDFormat()}-${agent.id}",
                            date = Date().time,
                            agent = "${agent.firstName} ${agent.lastName}",
                            agentId = agent.id,
                            amount = productList.sumOf { p -> p.second?.sumOf { pr -> pr.price ?: 0.00 } ?: 0.00 },
                            products = productList
                        )
                        addOrUpdateTransaction(transaction){ success, error ->
                            if(success){
                                trySend(Unit)
                            }else close(error)
                        }
                    }else {
                        close(it.exception)
                    }
                }
            awaitClose()
        }catch (e: Exception){
            close(e)
        }
    }

    override suspend fun verifyVoidCode(code: String): Flow<Unit> = callbackFlow {
        try {
            db.collection(FirestoreConstants.FIRESTORE_DOMAIN_COLLECTION)
                .document(sf.getDomain())
                .get().addOnCompleteListener {
                    if(it.isSuccessful){
                        val activationModel = it.result.toObject(ActivationModel::class.java)
                        if(activationModel?.voidCode.equals(code)) trySend(Unit)
                        else close(Exception("Invalid void code."))
                    } else close(it.exception)
                }
            awaitClose()
        }catch (e: Exception){
            close(e)
        }
    }

    private fun addOrUpdateTransaction(result: POSTransaction, function: (Boolean, Exception?) -> Unit) {
        try{
            db.collection(FirestoreConstants.FIRESTORE_DOMAIN_COLLECTION)
                .document(sf.getDomain())
                .collection(FirestoreConstants.FIRESTORE_POS_SESSION_COLLECTION)
                .document(result.id!!)
                .set(result, SetOptions.merge())
                .addOnCompleteListener {
                    if(it.isSuccessful) function(true, null)
                    else function(false, it.exception)
                }
        }catch (e: Exception){
            function(false, e)
        }
    }
}