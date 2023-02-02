package com.qr.pos.amper.auth.data.remote

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.qr.pos.amper.auth.data.dto.ActivationModel
import com.qr.pos.amper.auth.data.dto.SSOUser
import com.qr.pos.amper.auth.data.dto.User
import com.qr.pos.amper.auth.domain.repository.LoginRepo
import com.qr.pos.amper.common.domain.repository.SharedPrefRepo
import com.qr.pos.amper.common.utils.FirestoreConstants
import com.qr.pos.amper.utils.DateUtils
import com.qr.pos.amper.utils.network.NetResponse
import com.qr.pos.amper.utils.StringUtils
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class LoginRepoImp @Inject constructor(
    private val auth: FirebaseAuth,
    private val sf: SharedPrefRepo,
    private val db: FirebaseFirestore
): LoginRepo {
    override suspend fun login(username: String, password: String) = callbackFlow {
        try {
            auth.signInWithEmailAndPassword(username, password)
                .addOnCompleteListener { task ->
                    if(task.isSuccessful){
                        val user = task.result.user
                        val ssoUser = SSOUser(
                            isNewUser = task.result?.additionalUserInfo?.isNewUser,
                            user = User(
                                id = user?.uid,
                                email = user?.email
                            )
                        )
                        sf.setDomain(StringUtils.getDomain(user?.email ?: ""))
                        getUser(user?.uid ?: ""){ u, e ->
                            if(e != null) close(e)
                            else {
                                trySend(NetResponse.Success(ssoUser.copy(user = u)))
                                if(u != null && ssoUser.isNewUser == false) sf.setUser(u)
                            }
                        }
                    }else close(task.exception)
                }
            awaitClose()
        }catch (e: Exception){
            close(e)
        }
    }

    override suspend fun login(idToken: String, user: User): Flow<NetResponse<SSOUser>> = callbackFlow {
        try {
            auth.signInWithCredential(GoogleAuthProvider.getCredential(idToken, null))
                .addOnCompleteListener {
                    if(it.isSuccessful){
                        val resultUser = it.result.user
                        val ssoUser = SSOUser(
                            isNewUser = it.result.additionalUserInfo?.isNewUser == true
                        )
                        sf.setDomain(StringUtils.getDomain(resultUser?.email ?: ""))
                        getUser(resultUser?.uid ?: ""){ user, e ->
                            if(e != null) close(e)
                            else {
                                trySend(NetResponse.Success(ssoUser.copy(user = user)))
                                if(user != null && ssoUser.isNewUser == false) sf.setUser(user)
                            }
                        }
                    }else close(it.exception)
                }
            awaitClose()
        }catch (e: Exception){
            close(e)
        }
    }

    override suspend fun getActivationDuration(domain: String): Flow<ActivationModel> = callbackFlow {
        try {
            db.collection(FirestoreConstants.FIRESTORE_DOMAIN_COLLECTION)
                .document(domain)
                .get().addOnCompleteListener {
                    if(it.isSuccessful){
                        trySend(it.result.toObject(ActivationModel::class.java)!!)
                    }else close(it.exception)
                }
            awaitClose()
        }catch (e: Exception){
            close(e)
        }
    }

    override suspend fun activateBilling(activationCode: String, domain: String): Flow<ActivationModel> = callbackFlow {
        val document = db.collection(FirestoreConstants.FIRESTORE_DOMAIN_COLLECTION)
            .document(domain)
            .collection(FirestoreConstants.FIRESTORE_CODES_COLLECTION)
            .document(activationCode)
        try {
            document.get().addOnCompleteListener {
                if(it.isSuccessful){
                    val code = it.result.toObject(ActivationModel::class.java)
                    if(code == null){
                        close(Exception("Activation code is invalid."))
                    } else {
                        if(code.used == true){
                            close(Exception("Activation code is already used!"))
                        }else {
                            val startDate = DateUtils.getConvertedDate(sf.getServerDate())
                            val newCode = code.copy(
                                startDate = startDate.time,
                                endDate = DateUtils.datePlus(startDate, code.duration_days!!).time,
                                used = null
                            )
                            activateCode(newCode, domain) { e ->
                                if (e == null) {
                                    document.set(code.copy(used = true), SetOptions.merge())
                                    trySend(newCode)
                                } else close(e)
                            }
                        }
                    }
                }else close(it.exception)
            }
            awaitClose()
        }catch (e: Exception){
            close(e)
        }
    }

    private fun activateCode(code: ActivationModel, domain: String, error: (Exception?) -> Unit) {
        try {
            db.collection(FirestoreConstants.FIRESTORE_DOMAIN_COLLECTION)
                .document(domain)
                .set(code, SetOptions.merge())
                .addOnCompleteListener {
                    if(it.isSuccessful) error(null)
                    else error(it.exception)
                }
        }catch (e: Exception) {
            error(e)
        }
    }

    override fun logout() = auth.signOut()

    private fun getUser(userID: String, callback: (User?, Exception?) -> Unit) {
        db.collection(FirestoreConstants.FIRESTORE_DOMAIN_COLLECTION)
            .document(sf.getDomain())
            .collection(FirestoreConstants.FIRESTORE_USER_COLLECTION)
            .document(userID)
            .get()
            .addOnCompleteListener {
                if(it.isSuccessful){
                    val user = it.result.toObject(User::class.java)
                    callback(user, null)
                }else callback(null, it.exception)
            }
    }
}