package com.example.dynamicmessenger.common

import android.content.Context
import android.content.SharedPreferences
import com.example.dynamicmessenger.network.authorization.models.User
import com.example.dynamicmessenger.userDataController.UserDataManager
import java.lang.reflect.Array.get
import java.net.UnknownServiceException


object SharedConfigs {
    private lateinit var sharedPrefs: SharedPreferences

    fun init(context: Context) {
        sharedPrefs =  context.getSharedPreferences(SharedPrefConstants.sharedPrefCreate, Context.MODE_PRIVATE)
    }
    var shared = SharedConfigs

    var signedUser: User? = null

    var appLang: AppLangKeys
        get() {
            //todo get shared preferences app lang value
            // if lang from shared pref is null get OS preferred language and save it in shared preferences
            return AppLangKeys.valueOf(getAppLanguage())
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
        return sharedPrefs.getBoolean(SharedPrefConstants.sharedPrefDarkMode, false)
    }

    private fun setAppLanguage(appLang: String) {
        sharedPrefs
            .edit()
            .putString(SharedPrefConstants.sharedPrefAppLang, appLang)
            .apply()
    }

    private fun getAppLanguage(): String {
        return sharedPrefs.getString(SharedPrefConstants.sharedPrefAppLang, "EN") ?: "EN"
    }

}