package com.example.dynamicmessenger.activitys

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.dynamicmessenger.R
import com.example.dynamicmessenger.activitys.viewModels.HomeActivityViewModel
import com.example.dynamicmessenger.common.MyFragments
import com.example.dynamicmessenger.common.MyTime
import com.example.dynamicmessenger.common.SharedConfigs
import com.example.dynamicmessenger.network.UserTokenVerifyApi
import com.example.dynamicmessenger.network.authorization.models.User
import com.example.dynamicmessenger.userDataController.SharedPreferencesManager
import com.example.dynamicmessenger.userDataController.database.SignedUserDatabase
import com.example.dynamicmessenger.userDataController.database.UserTokenDao
import com.example.dynamicmessenger.userDataController.database.UserTokenRepository
import com.example.dynamicmessenger.userHome.fragments.UserCallFragment
import com.example.dynamicmessenger.userHome.fragments.UserChatFragment
import com.example.dynamicmessenger.userHome.fragments.UserInformationFragment
import com.example.dynamicmessenger.utils.ClassConverter
import com.example.dynamicmessenger.utils.LocalizationUtil
import com.example.dynamicmessenger.utils.NetworkUtils
import com.example.dynamicmessenger.utils.notifications.RemoteNotificationManager
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.iid.FirebaseInstanceId
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
    private lateinit var viewModel: HomeActivityViewModel
    private lateinit var tokenDao: UserTokenDao
    private lateinit var tokenRep: UserTokenRepository
    private lateinit var bottomNavBar: BottomNavigationView

    @SuppressLint("HardwareIds")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        bottomNavBar = findViewById(R.id.bottomNavigationView)
        bottomNavBar.setOnNavigationItemSelectedListener(navListener)
        tokenCheck(this, SharedConfigs.token)
        viewModel = ViewModelProvider(this).get(HomeActivityViewModel::class.java)
        tokenDao = SignedUserDatabase.getUserDatabase(this)!!.userTokenDao()
        tokenRep = UserTokenRepository(tokenDao)
        observers()
        //TODO change badge for all icons
        configureBadges()

        FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("FireBase", "getInstanceId failed", task.exception)
                return@OnCompleteListener
            }

            // Get new Instance ID token
            val token = task.result?.token

            // Log and toast
//            val msg = getString(R.string.msg_token_fmt, token)

            Log.d("FireBase", token)
//            Toast.makeText(baseContext, token, Toast.LENGTH_SHORT).show()
        })
        val androidId: String = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        SharedConfigs.deviceUUID = androidId
        RemoteNotificationManager.registerDeviceToken(androidId)

        SharedConfigs.appLang.value?.value?.let { LocalizationUtil.setApplicationLocale(this, it) }


    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(LocalizationUtil.updateResources(base!!, SharedConfigs.appLang.value!!.value))
    }

    override fun onDestroy() {
        super.onDestroy()
        activityJob.cancel()
    }

    override fun onStop() {
        super.onStop()
        SharedConfigs.currentFragment.value = MyFragments.NOT_SELECTED
    }

    private val navListener: BottomNavigationView.OnNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.call -> selectedFragment = UserCallFragment()
//                R.id.chanel -> selectedFragment = UserChanelFragment()
                R.id.chat -> selectedFragment = UserChatFragment()
//                R.id.group -> selectedFragment = UserGroupFragment()
                R.id.user -> selectedFragment = UserInformationFragment()
            }
            supportFragmentManager.beginTransaction().replace(
                R.id.fragmentContainer,
                selectedFragment
            ).commit()
            true
        }

    @SuppressLint("SimpleDateFormat")
    private fun tokenCheck(context: Context?, token: String) {
        coroutineScope.launch {
            try {
                val response = UserTokenVerifyApi.retrofitService.verifyUserToken(token)
                val tokenExpire = tokenRep.tokenExpire
                val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                val date: Date = format.parse(tokenExpire!!)!!
                val currentDate: Date = Calendar.getInstance().time
                if (response.body()?.tokenExists == false || date.time - currentDate.time <  MyTime.oneDay ) {
                    SharedPreferencesManager.deleteUserAllInformation(context!!)
                    SharedConfigs.deleteToken()
                    SharedConfigs.deleteSignedUser()
                    AlertDialog.Builder(context)
                        .setTitle(getString(R.string.error_message))
                        .setMessage(getString(R.string.your_session_expires_please_log_in_again))
                        .setPositiveButton(getString(R.string.ok)) { _, _ ->
                            val intent = Intent(context, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                        .create().show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Please check yur internet connection", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun configureBadges() {
        val callsBadge = bottomNavBar.getOrCreateBadge(R.id.call)
        val chatsBadge = bottomNavBar.getOrCreateBadge(R.id.chat)
        when (val missedCallHistorySize = SharedConfigs.signedUser?.missedCallHistory?.size) {
            0, null -> {
                callsBadge.isVisible = false
                Log.i("+++", "missed Call History Size $missedCallHistorySize")
            }
            else -> {
                callsBadge.isVisible = true
                callsBadge.number = missedCallHistorySize
                Log.i("+++", "missed Call History Size $missedCallHistorySize")
            }
        }

        when (SharedConfigs.chatsBadgesCount) {
            0 -> {
                chatsBadge.isVisible = false
                chatsBadge.number = -1
                Log.i("+++", "missed Chat History Size ${SharedConfigs.chatsBadgesCount}")
            }
            else -> {
                chatsBadge.isVisible = true
                chatsBadge.number = SharedConfigs.chatsBadgesCount
                Log.i("+++", "missed Chat History Size ${SharedConfigs.chatsBadgesCount}")
            }
        }
    }

    private fun observers() {
        NetworkUtils.getNetworkLiveData().observe(this , androidx.lifecycle.Observer {
            if (it) viewModel.getOnlineUsers()
        })

        SharedConfigs.userRepository.getUserInformation(SharedConfigs.signedUser?._id).observe(this, androidx.lifecycle.Observer { user ->
            user?.let {
                SharedConfigs.signedUser = ClassConverter.userToSignedUser(it).apply {
                    deviceRegistered = SharedConfigs.signedUser?.deviceRegistered!!
                }
            }
        })

        SharedConfigs.currentFragment.observe(this, androidx.lifecycle.Observer {
            when (it) {
                MyFragments.NOT_SELECTED,
                MyFragments.CALLS,
                MyFragments.CHATS,
                MyFragments.CONTACTS,
                MyFragments.INFORMATION -> {
                    bottomNavBar.visibility = View.VISIBLE
                }
                else -> bottomNavBar.visibility = View.GONE
            }
            Log.i("+++", "current fragment = $it")
        })
    }

    companion object {
        var opponentUser: User? = null
            set(value) {
                field = value
                Log.i("+++", "Opponent user set $value")
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
        var callId: String? = null
            set(value) {
                field = value
                Log.i("+++", "call Time set $value")
            }
    }
}
