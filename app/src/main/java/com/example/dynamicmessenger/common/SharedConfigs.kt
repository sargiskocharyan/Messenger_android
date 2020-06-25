package com.example.dynamicmessenger.common

import android.content.Context
import android.content.SharedPreferences
import com.example.dynamicmessenger.network.authorization.models.User
import com.example.dynamicmessenger.userDataController.UserDataManager
import java.lang.reflect.Array.get
import java.net.UnknownServiceException


object SharedConfigs {
    private lateinit var sharedPrefs: SharedPreferences

    init {
        println("Singleton class invoked.")
    }

    fun init(context: Context): SharedPreferences {
        return context.getSharedPreferences(SharedPrefConstants.sharedPrefCreate, Context.MODE_PRIVATE)
    }
    var shared = SharedConfigs

    var signedUser: User? = null

    var appLang : String  = AppLangKeys.EN
        get() {
            //todo get shared preferences app lang value
            // if lang from shared pref is null get OS preferred language and save it in shared preferences
             return AppLangKeys.EN
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

}