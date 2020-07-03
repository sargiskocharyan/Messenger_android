package com.example.dynamicmessenger.userHome.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.dynamicmessenger.R
import com.example.dynamicmessenger.common.SharedConfigs
import com.example.dynamicmessenger.userDataController.SaveToken
import com.example.dynamicmessenger.userDataController.SharedPreferencesManager
import com.example.dynamicmessenger.userDataController.database.SignedUser
import com.example.dynamicmessenger.userDataController.database.SignedUserDatabase
import com.example.dynamicmessenger.userDataController.database.SignedUserRepository
import com.example.dynamicmessenger.userDataController.database.UserTokenRepository


class UserCallFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val userDao = SignedUserDatabase.getSignedUserDatabase(requireContext())!!.signedUserDao()
        val userRep = SignedUserRepository(userDao)
        val tokenDao = SignedUserDatabase.getSignedUserDatabase(requireContext())!!.userTokenDao()
        val tokenRep = UserTokenRepository(tokenDao)
        Log.i("+++", tokenRep.getToken())
        Log.i("+++", SharedConfigs.signedUser.toString())
        userRep.signedUser.observe(requireActivity(), Observer {
            it.let {
                Log.i("+++", it.toString())
            }
        })

//            requireActivity(), Observer { words ->
            // Update the cached copy of the words in the adapter.
//            words?.let { adapter.setWords(it) }))
        return inflater.inflate(R.layout.fragment_user_call, container, false)
    }

}
