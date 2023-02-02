package com.qr.pos.amper.auth.data.remote

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.qr.pos.amper.auth.data.dto.User
import com.qr.pos.amper.auth.domain.repository.RegistrationRepo
import com.qr.pos.amper.common.utils.FirestoreConstants
import com.qr.pos.amper.utils.network.NetResponse
import com.qr.pos.amper.utils.StringUtils
import kotlinx.coroutines.channels.ChannelResult
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class RegisterRepoImp @Inject constructor(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth
): RegistrationRepo {

    override suspend fun validateEmail(email: String) = callbackFlow<NetResponse<Boolean>> {
        try {
            auth.fetchSignInMethodsForEmail(email)
                .addOnCompleteListener {
                    if(it.isSuccessful){
                        trySend(NetResponse.Success(it.result.signInMethods?.isEmpty() == true))
                    }else close(it.exception)
                }
            awaitClose()
        }catch (e: Exception){
            close(e)
        }
    }

    override suspend fun registerUser(user: User, password: String) = callbackFlow {
        try{
            auth.createUserWithEmailAndPassword(user.email ?: "", password)
                .addOnCompleteListener {
                    if(it.isSuccessful){
                        val registeredUser = user.copy(id = it.result.user?.uid)
                        addUsertoDatabase(registeredUser){ result ->
                            if(result.isSuccessful)trySend(NetResponse.Success(registeredUser))
                            else close(result.exception)
                        }
                    }else close(it.exception)
                }
            awaitClose()
        }catch (e: Exception){
            close(e)
        }
    }

    private fun addUsertoDatabase(registeredUser: User, callback: (Task<Void>) -> Unit ) {
        db.collection(FirestoreConstants.FIRESTORE_DOMAIN_COLLECTION)
            .document(StringUtils.getDomain(registeredUser.email ?: ""))
            .collection(FirestoreConstants.FIRESTORE_USER_COLLECTION)
            .document(registeredUser.id ?: "")
            .set(registeredUser)
            .addOnCompleteListener {
                callback(it)
            }
    }
}