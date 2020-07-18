package com.example.dynamicmessenger.userHome.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dynamicmessenger.R
import com.example.dynamicmessenger.databinding.FragmentUserCallBinding
import com.example.dynamicmessenger.userCalls.CallRoomActivity
import com.example.dynamicmessenger.userDataController.database.SignedUserDatabase
import com.example.dynamicmessenger.userDataController.database.UserCallsRepository
import com.example.dynamicmessenger.userHome.adapters.UserCallsAdapter

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

        val callsDao = SignedUserDatabase.getUserDatabase(requireContext())!!.userCallsDao()
        val callsRepository = UserCallsRepository(callsDao)

        val adapter = UserCallsAdapter(requireContext())
        binding.root.setHasTransientState(true)
        binding.callRecyclerView.adapter = adapter
        adapter.data = callsRepository.getUserCalls()
        val linearLayoutManager = LinearLayoutManager(requireContext())
        binding.callRecyclerView.layoutManager = linearLayoutManager

        return binding.root
    }

}
