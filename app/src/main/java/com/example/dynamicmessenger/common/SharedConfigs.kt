package com.example.dynamicmessenger.common

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import com.example.dynamicmessenger.network.authorization.models.User
import com.example.dynamicmessenger.userDataController.SharedPreferencesManager
import com.example.dynamicmessenger.userDataController.database.*
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
        sharedPrefs = context.getSharedPreferences(SharedPrefConstants.sharedPrefCreate, Context.MODE_PRIVATE)
    }

    var signedUser: SignedUser?
        get() {
            return userRep.signedUser.value
        }
        set(value) {
            if (value != null) {
                userRep.insert(value)
            }
        }

    var token: String?
        get() {
            return tokenRep.getToken()
        }
        set(value) {
            if (value != null) {
                tokenRep.insert(value)
            }
        }

    var appLang: AppLangKeys
        get() {
            return (if (getAppLanguage() == null || getAppLanguage() == "") {
                AppLangKeys.fromValue(Locale.getDefault().language)
            } else {
                AppLangKeys.valueOf(getAppLanguage()!!)
            })
        }
        set(value) {
            setAppLanguage(value.name)
        }


    fun setLang(key: String) {
        //1 set key to appLang
        // 2.  save in shared preferences
    }
//    var appMode: AppMode = when (getDarkMode()) {
//        true -> AppMode.dark
//        else -> AppMode.light
//    }
//
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