package com.example.dynamicmessenger.common

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.example.dynamicmessenger.userDataController.Repository
import com.example.dynamicmessenger.userDataController.database.*
import java.util.*


object SharedConfigs {
    private lateinit var sharedPrefs: SharedPreferences
    lateinit var myContext: Context
    private lateinit var userDao: SignedUserDao
    private lateinit var userRep: SignedUserRepository
    private lateinit var tokenDao: UserTokenDao
    private lateinit var tokenRep: UserTokenRepository
    var callingOpponentId: String? = null
    var callRoomName: String? = null
    var isCalling: Boolean = false //TODO
    var isCallingInProgress: Boolean = false
    var lastFragment: MyFragments? = null
    val currentFragment = MutableLiveData<MyFragments>()
    var onlineUsers = MutableLiveData<List<String>>()
    lateinit var userRepository: Repository
    fun init(context: Context) {
        this.myContext = context
        userDao = SignedUserDatabase.getUserDatabase(context)!!.signedUserDao()
        tokenDao = SignedUserDatabase.getUserDatabase(context)!!.userTokenDao()
        tokenRep = UserTokenRepository(tokenDao)
        userRep = SignedUserRepository(userDao)
        signedUser = userRep.signedUser
        sharedPrefs = context.getSharedPreferences(SharedPrefConstants.sharedPrefCreate, Context.MODE_PRIVATE)
        appLang.value = setLang()
        userRepository = Repository.getInstance(context)!!
    }

    var signedUser: SignedUser? = null
        set(value) {
            if (value != null) {
                field = value
                Log.i("+++userInsertIF", value.toString())
                userRep.update(value)
//                Log.i("+++userInsertrep", userRep.signedUser.toString())
            }
        }

    fun deleteAvatar() {
        signedUser?.avatarURL = null
        userRep.deleteAvatarFromRepository()
    }

    var token: String = ""

    fun saveToken(token: String, tokenExpire: String) {
        tokenRep.update(token, tokenExpire)
        this.token = token
    }

//    var appLang: AppLangKeys = AppLangKeys.EN
//        set(value) {
//            field = value
//            setAppLanguage(value.name)
//        }

    var appLang = MutableLiveData<AppLangKeys>(AppLangKeys.EN)

    fun changeAppLanguage(language: AppLangKeys) {
        appLang.value = language
        setAppLanguage(language.name)
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