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
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import com.example.dynamicmessenger.R
import com.example.dynamicmessenger.activitys.viewModels.HomeActivityViewModel
import com.example.dynamicmessenger.common.ResponseUrls
import com.example.dynamicmessenger.common.SharedConfigs
import com.example.dynamicmessenger.network.UserTokenVerifyApi
import com.example.dynamicmessenger.network.authorization.models.Chat
import com.example.dynamicmessenger.network.authorization.models.User
import com.example.dynamicmessenger.network.chatRooms.SocketManager
import com.example.dynamicmessenger.userCalls.CallRoomActivity
import com.example.dynamicmessenger.userCalls.SocketEventsForVideoCalls
import com.example.dynamicmessenger.userDataController.SharedPreferencesManager
import com.example.dynamicmessenger.userDataController.database.SignedUserDatabase
import com.example.dynamicmessenger.userDataController.database.UserTokenDao
import com.example.dynamicmessenger.userDataController.database.UserTokenRepository
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
import java.text.SimpleDateFormat
import java.util.*


class HomeActivity : AppCompatActivity() {
    private var activityJob = Job()
    private val coroutineScope = CoroutineScope(activityJob + Dispatchers.Main)
    private lateinit var selectedFragment: Fragment
    private lateinit var socketManager: SocketManager
    private lateinit var mSocket: Socket
    private lateinit var viewModel: HomeActivityViewModel
    private lateinit var tokenDao: UserTokenDao
    private lateinit var tokenRep: UserTokenRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        val bottomNavBar: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavBar.setOnNavigationItemSelectedListener(navListener)
        tokenCheck(this, SharedConfigs.token)
        viewModel = ViewModelProvider(this).get(HomeActivityViewModel::class.java)
        tokenDao = SignedUserDatabase.getUserDatabase(this)!!.userTokenDao()
        tokenRep = UserTokenRepository(tokenDao)

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

        mSocket.on("callAccepted") {
            socketManager.onCallAccepted(it)
            Log.d("SignallingClientAcc", "Home activity call accepted: args = " + Arrays.toString(it))
        }
        mSocket.on("offer") {
            socketManager.onOffer(it)
            Log.d("SignallingClientAcc", "Home activity offer ")
        }
        mSocket.on("answer") {
            socketManager.onAnswer(it)
            Log.d("SignallingClientAcc", "Home activity answer ")
        }
        mSocket.on("candidates") {
            socketManager.onCandidate(it)
            Log.d("SignallingClientAcc", "Home activity candidates ")
        }
        mSocket.on("call") {
            if (!SharedConfigs.isCallingInProgress) {
                SharedConfigs.callingOpponentId = it[0].toString()
                SharedConfigs.isCalling = true
                val intent = Intent(this, CallRoomActivity::class.java)
                startActivity(intent)
            } else {
                mSocket.emit("callAccepted", SharedConfigs.callingOpponentId, false)
                NotificationMessages.setNotificationMessage("Incoming Call", "Incoming Call", this, manager)
            }
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
                val tokenExpire = tokenRep.tokenExpire
                val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                val date: Date = format.parse(tokenExpire!!)!!
                val currentDate: Date = Calendar.getInstance().time
                if (response.body()?.tokenExists == false || date.time - currentDate.time <  86400000 ) {
                    SharedPreferencesManager.deleteUserAllInformation(context!!)
                    SharedConfigs.deleteToken()
                    SharedConfigs.deleteSignedUser()
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
