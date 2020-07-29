package com.example.dynamicmessenger.activitys

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import com.example.dynamicmessenger.R
import com.example.dynamicmessenger.common.SharedConfigs
import com.example.dynamicmessenger.databinding.ActivityMainBinding
import com.example.dynamicmessenger.userDataController.database.SignedUserDatabase
import com.example.dynamicmessenger.userDataController.database.UserTokenDao
import com.example.dynamicmessenger.userDataController.database.UserTokenRepository
import com.example.dynamicmessenger.utils.LocalizationUtil
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var tokenDao: UserTokenDao
    private lateinit var tokenRep: UserTokenRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this,
            R.layout.activity_main
        )
        tokenDao = SignedUserDatabase.getUserDatabase(this)!!.userTokenDao()
        tokenRep = UserTokenRepository(tokenDao)
        //TODO: binding adapter
        changeDarkMode()

        if (tokenRep.getToken() != "") {
            SharedConfigs.token = tokenRep.getToken()
            val intent = Intent(this,HomeActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        } else {
            binding.drawerLayout.visibility = View.VISIBLE
        }
        drawerLayout = binding.drawerLayout

        val navController = this.findNavController(R.id.myNavHostFragment)
//        NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout) TODO
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(LocalizationUtil.updateResources(base!!, SharedConfigs.appLang.value!!.value))
    }

    private fun changeDarkMode() {
        if (SharedConfigs.getDarkMode()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

}

