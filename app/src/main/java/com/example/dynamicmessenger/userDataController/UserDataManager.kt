package com.example.dynamicmessenger.userDataController

import android.content.Context
import android.content.SharedPreferences
import com.example.dynamicmessenger.common.SharedPrefConstants
import com.example.dynamicmessenger.network.authorization.models.User
import com.google.gson.Gson

object UserDataManager {
    private lateinit var sharedPrefs: SharedPreferences

    fun init(context: Context) {
        sharedPrefs = context.getSharedPreferences(SharedPrefConstants.sharedPrefCreate, Context.MODE_PRIVATE)
    }

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