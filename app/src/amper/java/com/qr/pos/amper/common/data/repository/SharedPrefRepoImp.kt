package com.qr.pos.amper.common.data.repository

import android.content.Context
import android.content.ContextWrapper
import com.google.gson.Gson
import com.qr.pos.amper.BuildConfig
import com.qr.pos.amper.auth.data.dto.User
import com.qr.pos.amper.auth.utils.UserType
import com.qr.pos.amper.common.domain.repository.SharedPrefRepo
import javax.inject.Inject

class SharedPrefRepoImp @Inject constructor(
    context: Context
): ContextWrapper(context), SharedPrefRepo {

    private val sharedPreferences = getSharedPreferences(BuildConfig.APPLICATION_ID, MODE_PRIVATE)
    private val editor = sharedPreferences.edit()
    private val gson = Gson()

    override fun setDomain(domain: String) = editor.putString(DOMAIN_NAME, domain).apply()

    override fun getDomain(): String = sharedPreferences.getString(DOMAIN_NAME, "") ?: ""

    override fun setUser(user: User) = editor.putString(USER, gson.toJson(user)).apply()

    override fun getUser(): User = gson.fromJson(sharedPreferences.getString(USER, ""), User::class.java)

    override fun getUserType(): UserType = UserType.fromTypeKey(getUser().userTypeKey ?: 1)

    override fun getServerDate(): String = sharedPreferences.getString(SERVER_TIME, "") ?: ""

    override fun setServerDate(dateStr: String) = editor.putString(SERVER_TIME, dateStr).apply()

    companion object {
        const val DOMAIN_NAME = "domain_name"
        const val USER = "user"
        const val SERVER_TIME = "server_time"
    }
}