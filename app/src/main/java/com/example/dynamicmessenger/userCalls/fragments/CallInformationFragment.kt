package com.example.dynamicmessenger.userCalls.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.dynamicmessenger.R
import com.example.dynamicmessenger.activitys.HomeActivity
import com.example.dynamicmessenger.common.MyFragments
import com.example.dynamicmessenger.common.SharedConfigs
import com.example.dynamicmessenger.databinding.FragmentCallInformationBinding
import com.example.dynamicmessenger.userCalls.CallRoomActivity
import com.example.dynamicmessenger.userCalls.viewModels.CallInformationViewModel
import com.example.dynamicmessenger.userChatRoom.fragments.ChatRoomFragment

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

        SharedConfigs.currentFragment.value = MyFragments.CALL_INFORMATION

        //Toolbar
        setHasOptionsMenu(true)
        val toolbar: Toolbar = binding.callInformationToolBar
        configureTopNavBar(toolbar)

        viewModel.callInformation.observe(viewLifecycleOwner, Observer { userCalls ->
            if (userCalls != null) {
                val opponentId = HomeActivity.receiverID
                Log.i("+++", "opponent Id $opponentId")
                SharedConfigs.userRepository.getUserInformation(opponentId).observe(viewLifecycleOwner, Observer { user ->
                    viewModel.opponentInformation.value = user
                    HomeActivity.opponentUser = user
                    SharedConfigs.userRepository.getAvatar(user?.avatarURL).observe(viewLifecycleOwner, Observer {
                        viewModel.opponentAvatarBitmap.value = it
                    })
                })
                viewModel.configureCallInformation(userCalls)
            }
//            Log.i("+++", "opponent Id ${userCalls.participants}")
        })


        binding.callInformationMessageImageView.setOnClickListener {
            HomeActivity.isAddContacts = null
            (context as AppCompatActivity?)!!.supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragmentContainer, ChatRoomFragment())
                .addToBackStack(null)
                .commit()
        }

        binding.callInformationVideoCallImageView.setOnClickListener {
            SharedConfigs.callingOpponentId = HomeActivity.opponentUser!!._id
            val intent = Intent(activity, CallRoomActivity::class.java)
            startActivity(intent)
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