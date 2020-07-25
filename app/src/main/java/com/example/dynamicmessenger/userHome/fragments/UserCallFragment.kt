package com.example.dynamicmessenger.userHome.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dynamicmessenger.databinding.FragmentUserCallBinding
import com.example.dynamicmessenger.userDataController.database.SignedUserDatabase
import com.example.dynamicmessenger.userDataController.database.UserCalls
import com.example.dynamicmessenger.userDataController.database.UserCallsRepository
import com.example.dynamicmessenger.userHome.adapters.UserCallsAdapter

class UserCallFragment : Fragment() {

    private lateinit var binding: FragmentUserCallBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserCallBinding.inflate(layoutInflater)

        val callsDao = SignedUserDatabase.getUserDatabase(requireContext())!!.userCallsDao()
        val callsRepository = UserCallsRepository(callsDao)

        val adapter = UserCallsAdapter(requireContext())
        binding.root.setHasTransientState(true)
        binding.callRecyclerView.adapter = adapter
        binding.lifecycleOwner = this
        val userCalls: LiveData<List<UserCalls>> = callsRepository.getUserCalls
        userCalls.observe(viewLifecycleOwner, Observer {
            adapter.setAdapterData(it)
        })
        val linearLayoutManager = LinearLayoutManager(requireContext())
        binding.callRecyclerView.layoutManager = linearLayoutManager

        return binding.root
    }

}
