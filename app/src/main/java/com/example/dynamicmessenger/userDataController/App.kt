package com.example.dynamicmessenger.userDataController

import android.app.Application
import android.content.Context
import com.example.dynamicmessenger.common.SharedConfigs
import com.example.dynamicmessenger.utils.LocalizationUtil

class App: Application() {
    override fun onCreate() {
        super.onCreate()
        UserDataManager.init(this)
        SharedConfigs.init(this)
    }

//    override fun attachBaseContext(base: Context?) {
//        super.attachBaseContext(LocalizationUtil.applyLanguage(base!!, SharedConfigs.appLang.value))
//    }
}
