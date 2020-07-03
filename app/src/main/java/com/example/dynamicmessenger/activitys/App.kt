package com.example.dynamicmessenger.activitys

import android.app.Application
import android.util.Log
import com.example.dynamicmessenger.common.SharedConfigs

class App: Application() {
    override fun onCreate() {
        super.onCreate()
        Log.i("+++called App  ", "called")
        SharedConfigs.init(this)
    }

//    override fun attachBaseContext(base: Context?) {
//        val lang = SharedConfigs.appLang.value ?: Locale.getDefault().getLanguage()
//        super.attachBaseContext(LocalizationUtil.applyLanguage(base!!, lang))
//    }
}
