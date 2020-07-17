package com.example.dynamicmessenger.userHome.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.dynamicmessenger.R
import com.example.dynamicmessenger.activitys.MainActivity
import com.example.dynamicmessenger.databinding.FragmentChatRoomBinding
import com.example.dynamicmessenger.databinding.FragmentUserCallBinding
import com.example.dynamicmessenger.userCalls.CallRoomActivity


class UserCallFragment : Fragment() {

    private lateinit var binding: FragmentUserCallBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_user_call,
            container,false)

        binding.goToCallActivityButton.setOnClickListener {
            val intent = Intent(activity, CallRoomActivity::class.java)
            startActivity(intent)
            (activity as Activity?)!!.overridePendingTransition(1, 1)
        }

        return binding.root
    }

}
