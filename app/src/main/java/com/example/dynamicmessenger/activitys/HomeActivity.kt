package com.example.dynamicmessenger.activitys

import android.app.Activity
import android.app.AlertDialog
import android.app.NotificationManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import com.example.dynamicmessenger.R
import com.example.dynamicmessenger.common.ResponseUrls
import com.example.dynamicmessenger.common.SharedConfigs
import com.example.dynamicmessenger.network.UserTokenVerifyApi
import com.example.dynamicmessenger.network.authorization.models.Chat
import com.example.dynamicmessenger.network.authorization.models.User
import com.example.dynamicmessenger.network.chatRooms.SocketManager
import com.example.dynamicmessenger.userCalls.CallRoomActivity
import com.example.dynamicmessenger.userHome.fragments.*
import com.example.dynamicmessenger.utils.LocalizationUtil
import com.example.dynamicmessenger.utils.MyAlertMessage
import com.example.dynamicmessenger.utils.NotificationMessages
import com.github.nkzawa.socketio.client.IO
import com.github.nkzawa.socketio.client.Socket
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*


class HomeActivity : AppCompatActivity() {
    private var activityJob = Job()
    private val coroutineScope = CoroutineScope(activityJob + Dispatchers.Main)
    private lateinit var selectedFragment: Fragment
    private lateinit var socketManager: SocketManager
    private lateinit var mSocket: Socket

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
//        this.supportActionBar!!.hide()  TODO
        val bottomNavBar: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavBar.setOnNavigationItemSelectedListener(navListener)
        val context = this
//        if (SharedConfigs.token == "") tokenCheck(context, SharedConfigs.token)

        //socket
        socketManager = SocketManager
        try {
            mSocket = socketManager.getSocketInstance()!!
        } catch (e: Exception){
            //TODO: Use TAGS
            Log.e("+++", "HomeActivity Socket $e")
        }
        mSocket.connect()
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        mSocket.on("message", socketManager.onMessageForNotification(Activity()){
            try {
                Log.i("+++mSocket", selectedFragment.toString())
                if (selectedFragment != UserChatFragment() && it.sender.id != SharedConfigs.signedUser?._id ?: true){
                    NotificationMessages.setNotificationMessage(it.sender.name, it.text, this, manager)
                }
            } catch (e: Exception) {
                Log.i("+++catch", e.toString())
            }
        })

        mSocket.on("call") {
            SharedConfigs.callingOpponentId = it[0].toString()
            SharedConfigs.isCalling = true
            val intent = Intent(this, CallRoomActivity::class.java)
            startActivity(intent)
        }

        SharedConfigs.appLang.observe(this, androidx.lifecycle.Observer {
            LocalizationUtil.setApplicationLocale(this, it.value)
        })
    }

//    override fun attachBaseContext(base: Context?) {
//        super.attachBaseContext(LocalizationUtil.updateResources(base!!, SharedConfigs.appLang.value!!.value))
//    }

    override fun onDestroy() {
        super.onDestroy()
        socketManager.closeSocket()
        activityJob.cancel()
    }

    private val navListener: BottomNavigationView.OnNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.call -> selectedFragment = UserCallFragment()
                R.id.chanel -> selectedFragment = UserChanelFragment()
                R.id.chat -> selectedFragment = UserChatFragment()
                R.id.group -> selectedFragment = UserGroupFragment()
                R.id.user -> selectedFragment = UserInformationFragment()
            }
            Log.i("+++BottomNavigationView", selectedFragment.toString())
            supportFragmentManager.beginTransaction().replace(
                R.id.fragmentContainer,
                selectedFragment
            ).commit()
            true
        }

    private fun tokenCheck(context: Context?, token: String) {
        coroutineScope.launch {
            try {
                val response = UserTokenVerifyApi.retrofitService.userTokenResponseAsync(token)
                if (response.isSuccessful) {
                    if (!response.body()!!.tokenExists) {
                        SharedConfigs.token = ""
                        AlertDialog.Builder(context)
                            .setTitle("Error")
                            .setMessage("Your seans is out of time")
                            .setPositiveButton("ok") { _, _ ->
                                val intent = Intent(context, MainActivity::class.java)
                                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                startActivity(intent)
                            }
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

    companion object {
        var opponentUser: User? = null
            set(value) {
                field = value
                Log.i("+++", "Opponent user set $value")
            }
        var receiverChatInfo: Chat? = null
            set(value) {
                field = value
                Log.i("+++", "receiver Chat Info set $value")
            }
        var receiverID: String? = null
            set(value) {
                field = value
                Log.i("+++", "receiver id set $value")
            }
        var isAddContacts: Boolean? = null
            set(value) {
                field = value
                Log.i("+++", "is Add Contacts set $value")
            }
    }
}
