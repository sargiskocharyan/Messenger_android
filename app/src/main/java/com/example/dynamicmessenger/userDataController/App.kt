package com.example.dynamicmessenger.userDataController

import android.app.Application
import com.example.dynamicmessenger.common.SharedConfigs

class App: Application() {
    override fun onCreate() {
        super.onCreate()
        UserDataManager.init(this)
        SharedConfigs.init(this)
    }
}
