package com.example.dynamicmessenger.userChatRoom.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.dynamicmessenger.R
import com.example.dynamicmessenger.activitys.HomeActivity
import com.example.dynamicmessenger.activitys.MainActivity
import com.example.dynamicmessenger.common.SharedConfigs
import com.example.dynamicmessenger.databinding.FragmentOpponentInformationBinding
import com.example.dynamicmessenger.userCalls.CallRoomActivity
import com.example.dynamicmessenger.userChatRoom.viewModels.OpponentInformationViewModel
import com.example.dynamicmessenger.userDataController.database.SignedUserDatabase
import com.example.dynamicmessenger.userDataController.database.UserCalls
import com.example.dynamicmessenger.userDataController.database.UserCallsRepository
import java.util.*

class OpponentInformationFragment : Fragment() {

    private lateinit var viewModel: OpponentInformationViewModel
    private lateinit var binding: FragmentOpponentInformationBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(OpponentInformationViewModel::class.java)
        binding = FragmentOpponentInformationBinding.inflate(layoutInflater)

        val callsDao = SignedUserDatabase.getUserDatabase(requireContext())!!.userCallsDao()
        val callsRepository = UserCallsRepository(callsDao)

        //Toolbar
        setHasOptionsMenu(true)
        val toolbar: Toolbar = binding.opponentInformationToolbar
        configureTopNavBar(toolbar)

        viewModel.getAvatar {
            binding.opponentProfileAvatarImageView.setImageBitmap(it)
        }

        binding.addToContactsTextView.setOnClickListener {
            viewModel.addUserToContacts {
                if (it) {
                    binding.addToContactsTextView.visibility = View.INVISIBLE
                }
            }
        }

        binding.callOpponentImageView.setOnClickListener {
            val opponentUser = HomeActivity.opponentUser!!
            val currentDate = System.currentTimeMillis()
            val userCalls = UserCalls(opponentUser._id, opponentUser.name , opponentUser.lastname, opponentUser.username, opponentUser.avatarURL, currentDate)
            SharedConfigs.callingOpponentId = opponentUser._id
            callsRepository.insert(userCalls)
            val intent = Intent(activity, CallRoomActivity::class.java)
            startActivity(intent)
            (activity as Activity?)!!.overridePendingTransition(1, 1)
        }

        binding.sendMessageImageView.setOnClickListener {
            HomeActivity.isAddContacts = null
            (context as AppCompatActivity?)!!.supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragmentContainer, ChatRoomFragment())
                .addToBackStack(null)
                .commit()
        }

        configurePage()


        return binding.root
    }

    private fun configureTopNavBar(toolbar: Toolbar) {
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        toolbar.elevation = 10.0F
        toolbar.setNavigationOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    private fun configurePage() {
        val opponentUser = HomeActivity.opponentUser
        //TODO:binding
        binding.opponentInformationToolbarTextView.text = opponentUser?.username
        binding.opponentInfo.text = opponentUser?.info
        binding.opponentInfoUsernameTextView.text = opponentUser?.username
        binding.opponentInfoNameTextView.text = opponentUser?.name
        binding.opponentInfoLastNameTextView.text = opponentUser?.lastname
        binding.opponentInfoPhoneTextView.text = opponentUser?.phoneNumber
        binding.opponentInfoEmailTextView.text = opponentUser?.email
        binding.opponentInfoGenderTextView.text = opponentUser?.gender
        binding.opponentInfoBirthDateTextView.text = opponentUser?.birthday?.substring(0, 10)
        binding.opponentInfoAddressTextView.text = opponentUser?.address

        if (HomeActivity.isAddContacts == false) {
            binding.addToContactsTextView.visibility = View.INVISIBLE
        } else if (HomeActivity.isAddContacts == null) {
            binding.addToContactsTextView.visibility = View.INVISIBLE
            binding.sendMessageImageView.visibility = View.INVISIBLE
        }
    }
}