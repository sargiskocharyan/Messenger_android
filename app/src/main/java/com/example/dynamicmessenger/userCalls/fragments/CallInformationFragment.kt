package com.example.dynamicmessenger.userCalls.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import com.example.dynamicmessenger.R
import com.example.dynamicmessenger.activitys.HomeActivity
import com.example.dynamicmessenger.common.SharedConfigs
import com.example.dynamicmessenger.databinding.FragmentCallInformationBinding
import com.example.dynamicmessenger.userCalls.CallRoomActivity
import com.example.dynamicmessenger.userCalls.viewModels.CallInformationViewModel
import com.example.dynamicmessenger.userChatRoom.fragments.ChatRoomFragment
import com.example.dynamicmessenger.userDataController.database.UserCalls

class CallInformationFragment : Fragment() {

    private lateinit var viewModel: CallInformationViewModel
    private lateinit var binding: FragmentCallInformationBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(CallInformationViewModel::class.java)
        binding = FragmentCallInformationBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        //Toolbar
        setHasOptionsMenu(true)
        val toolbar: Toolbar = binding.callInformationToolBar
        configureTopNavBar(toolbar)

        HomeActivity.callTime?.let {
            viewModel.callInformationByTime(it)
        }

        binding.callInformationMessageImageView.setOnClickListener {
            HomeActivity.isAddContacts = null
            (context as AppCompatActivity?)!!.supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragmentContainer, ChatRoomFragment())
                .addToBackStack(null)
                .commit()
        }

        binding.callInformationVideoCallImageView.setOnClickListener {
            val opponentUser = HomeActivity.opponentUser!!
            val currentDate = System.currentTimeMillis()
            val userCalls = UserCalls(opponentUser._id, opponentUser.name , opponentUser.lastname, opponentUser.username, opponentUser.avatarURL, currentDate, 1)
            SharedConfigs.callingOpponentId = opponentUser._id
            viewModel.saveCall(userCalls)
            val intent = Intent(activity, CallRoomActivity::class.java)
            startActivity(intent)
            (activity as Activity?)!!.overridePendingTransition(1, 1)
        }

        return binding.root
    }

    private fun configureTopNavBar(toolbar: Toolbar) {
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }
}