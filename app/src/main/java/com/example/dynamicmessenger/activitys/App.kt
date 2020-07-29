package com.example.dynamicmessenger.activitys

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.Observer
import com.example.dynamicmessenger.common.SharedConfigs
import com.example.dynamicmessenger.userDataController.database.SignedUserDatabase
import com.example.dynamicmessenger.userDataController.database.UserTokenRepository
import com.example.dynamicmessenger.utils.LocalizationUtil
import java.text.SimpleDateFormat
import java.util.*

class App: Application() {
    override fun onCreate() {
        super.onCreate()
        SharedConfigs.init(this)
//        SharedConfigs.appLang.observe(this, Observer {
//                super.attachBaseContext(LocalizationUtil.updateResources(this, it.value))
//
//        })
    }

    //    override fun attachBaseContext(base: Context?) {
//        val lang = SharedConfigs.appLang.value ?: Locale.getDefault().getLanguage()
//        super.attachBaseContext(LocalizationUtil.applyLanguage(base!!, lang))
//    }
}
