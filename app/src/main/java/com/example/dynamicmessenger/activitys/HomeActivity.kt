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
import com.example.dynamicmessenger.network.authorization.UserTokenVerifyApi
import com.example.dynamicmessenger.network.authorization.models.UserTokenProperty
import com.example.dynamicmessenger.userDataController.SaveToken
import com.example.dynamicmessenger.userDataController.SharedPreferencesManager
import com.example.dynamicmessenger.userHome.fragments.*
import com.example.dynamicmessenger.utils.MyAlertMessage
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        (this as AppCompatActivity?)!!.supportActionBar!!.hide()
        val bottomNavBar: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavBar.setOnNavigationItemSelectedListener(navListener)
        val myEncryptedToken = SharedPreferencesManager.getUserToken(this)
        val myToken = SaveToken.decrypt(myEncryptedToken)
        val context = this
        tokenCheck(context, myToken)
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
        val getProperties: Call<UserTokenProperty> = UserTokenVerifyApi.retrofitService.userTokenResponse(token)
        try {
            getProperties.enqueue(object : Callback<UserTokenProperty?> {
                override fun onResponse(
                    call: Call<UserTokenProperty?>,
                    response: Response<UserTokenProperty?>
                ) {
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
                }
                override fun onFailure(
                    call: Call<UserTokenProperty?>,
                    t: Throwable
                ) {
                    Toast.makeText(context, "Please check yur internet connection", Toast.LENGTH_SHORT).show()
                }
            })
        } catch (e: Exception) {
        }
    }
}
