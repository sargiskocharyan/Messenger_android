package com.example.dynamicmessenger.activitys

import android.app.Application
import com.example.dynamicmessenger.common.SharedConfigs

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
