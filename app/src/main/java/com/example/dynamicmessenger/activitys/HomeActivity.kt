package com.example.dynamicmessenger.activitys

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.dynamicmessenger.R
import com.example.dynamicmessenger.common.SharedConfigs
import com.example.dynamicmessenger.network.authorization.UserTokenVerifyApi
import com.example.dynamicmessenger.userDataController.SaveToken
import com.example.dynamicmessenger.userDataController.SharedPreferencesManager
import com.example.dynamicmessenger.userHome.fragments.*
import com.example.dynamicmessenger.utils.LocalizationUtil
import com.example.dynamicmessenger.utils.MyAlertMessage
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class HomeActivity : AppCompatActivity() {
    private var activityJob = Job()
    private val coroutineScope = CoroutineScope(activityJob + Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SharedPreferencesManager.loadUserObjectToSharedConfigs(this)
        setContentView(R.layout.activity_home)
        (this as AppCompatActivity?)!!.supportActionBar!!.hide()
        val bottomNavBar: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavBar.setOnNavigationItemSelectedListener(navListener)
        val myEncryptedToken = SharedPreferencesManager.getUserToken(this)
        val myToken = SaveToken.decrypt(myEncryptedToken)
        val context = this
        if (myToken != null) tokenCheck(context, myToken)
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(LocalizationUtil.updateResources(base!!, SharedConfigs.appLang.value))
    }

    override fun onDestroy() {
        super.onDestroy()
        activityJob.cancel()
    }

    private val navListener: BottomNavigationView.OnNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            var selectedFragment: Fragment? = null
            when (item.itemId) {
                R.id.call -> selectedFragment = UserCallFragment()
                R.id.chanel -> selectedFragment = UserChanelFragment()
                R.id.chat -> selectedFragment = UserChatFragment()
                R.id.group -> selectedFragment = UserGroupFragment()
                R.id.user -> selectedFragment = UserInformationFragment()
            }
            supportFragmentManager.beginTransaction().replace(
                R.id.fragmentContainer,
                selectedFragment!!
            ).commit()
            true
        }

    private fun tokenCheck(context: Context?, token: String) {
        coroutineScope.launch {
            try {
                val response = UserTokenVerifyApi.retrofitService.userTokenResponseAsync(token).await()
                if (response.isSuccessful) {
                    if (!response.body()!!.tokenExists) {
                        SharedPreferencesManager.setUserToken(context!!, "")
                        AlertDialog.Builder(context)
                            .setTitle("Error")
                            .setMessage("Your seans is out of time")
                            .setPositiveButton("ok",
                                DialogInterface.OnClickListener { dialog, which ->
                                    val intent = Intent(context, MainActivity::class.java)
                                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                    startActivity(intent)
                                })
                            .create().show()
                    }
                } else {
                    MyAlertMessage.showAlertDialog(context, "Error verify token")
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Please check yur internet connection", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
