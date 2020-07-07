package com.example.dynamicmessenger.userHome.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.dynamicmessenger.R
import com.example.dynamicmessenger.common.SharedConfigs
import com.example.dynamicmessenger.userDataController.database.SignedUserDatabase
import com.example.dynamicmessenger.userDataController.database.SignedUserRepository
import com.example.dynamicmessenger.userDataController.database.UserTokenRepository


class UserCallFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
//        val userDao = SignedUserDatabase.getUserDatabase(requireContext())!!.signedUserDao()
//        val userRep = SignedUserRepository(userDao)
//        val tokenDao = SignedUserDatabase.getUserDatabase(requireContext())!!.userTokenDao()
//        val tokenRep = UserTokenRepository(tokenDao)
//        Log.i("+++token", tokenRep.getToken())
//        Log.i("+++SharedConfigsToken", SharedConfigs.token)
//        Log.i("+++SharedConfigs", SharedConfigs.signedUser.toString())
//        Log.i("+++userRepo", userRep.signedUser.toString())

        return inflater.inflate(R.layout.fragment_user_call, container, false)
    }

}
