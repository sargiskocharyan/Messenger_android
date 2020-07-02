package com.example.dynamicmessenger.common

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.dynamicmessenger.userDataController.database.*
import kotlinx.coroutines.*
import java.util.*


object SharedConfigs {
    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var context: Context
    private lateinit var userDao: SignedUserDao
    private lateinit var tokenDao: UserTokenDao
    private lateinit var tokenRep: UserTokenRepository
    private lateinit var userRep: SignedUserRepository
    fun init(context: Context) {
        this.context = context
        userDao = SignedUserDatabase.getSignedUserDatabase(context)!!.signedUserDao()
        tokenDao = SignedUserDatabase.getSignedUserDatabase(context)!!.userTokenDao()
        userRep = SignedUserRepository(userDao)
        tokenRep = UserTokenRepository(tokenDao)
        signedUser = userRep.signedUser
        Log.i("+++userRep", userRep.signedUser.toString())
//        Log.i("+++tokenRep", tokenRep.getToken())
        token = tokenRep.getToken()
        sharedPrefs = context.getSharedPreferences(SharedPrefConstants.sharedPrefCreate, Context.MODE_PRIVATE)
        appLang = setLang()
    }

    var signedUser: SignedUser? = null
        set(value) {
            if (value != null) {
                field = value
                Log.i("+++userInsertIF", value.toString())
                userRep.insert(value)
                Log.i("+++userInsertrep", userRep.signedUser.toString())
            }
        }

    var token: String = ""
        set(value) {
            field = value
            tokenRep.insert(value)
        }

    var appLang: AppLangKeys = AppLangKeys.EN
        set(value) {
            field = value
            setAppLanguage(value.name)
        }


    private fun setLang(): AppLangKeys {
        return (if (getAppLanguage() == null || getAppLanguage() == "") {
            AppLangKeys.fromValue(Locale.getDefault().language)
        } else {
            AppLangKeys.valueOf(getAppLanguage()!!)
        })
    }
//    var appMode: AppMode = when (getDarkMode()) {
//        true -> AppMode.dark
//        else -> AppMode.light
//    }
//
    fun deleteSignedUser() {
        signedUser = null
        userRep.delete()
    }

    fun deleteToken() {
        token = ""
        tokenRep.delete()
    }

    fun setDarkMode(enabled: Boolean) {
        sharedPrefs
            .edit()
            .putBoolean(SharedPrefConstants.sharedPrefDarkMode, enabled)
            .apply()
    }

    fun getDarkMode(): Boolean {
        return sharedPrefs.getBoolean(SharedPrefConstants.sharedPrefDarkMode, false) ?: false
    }

    private fun setAppLanguage(appLang: String) {
        sharedPrefs
            .edit()
            .putString(SharedPrefConstants.sharedPrefAppLang, appLang)
            .apply()
    }

    private fun getAppLanguage(): String? {
        return sharedPrefs.getString(SharedPrefConstants.sharedPrefAppLang, "")
    }
}