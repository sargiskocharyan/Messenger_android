package com.example.dynamicmessenger.userHome.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dynamicmessenger.databinding.FragmentUserCallBinding
import com.example.dynamicmessenger.userDataController.database.SignedUserDatabase
import com.example.dynamicmessenger.userDataController.database.UserCalls
import com.example.dynamicmessenger.userDataController.database.UserCallsDao
import com.example.dynamicmessenger.userDataController.database.UserCallsRepository
import com.example.dynamicmessenger.userHome.adapters.UserCallsAdapter
import com.example.dynamicmessenger.userHome.viewModels.UserCallViewModel

class UserCallFragment : Fragment() {

    private lateinit var binding: FragmentUserCallBinding
    private lateinit var callsDao: UserCallsDao
    private lateinit var callsRepository: UserCallsRepository
    private lateinit var adapter: UserCallsAdapter
    private lateinit var viewModel: UserCallViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserCallBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this).get(UserCallViewModel::class.java)
        callsDao = SignedUserDatabase.getUserDatabase(requireContext())!!.userCallsDao()
        callsRepository = UserCallsRepository(callsDao)
        adapter = UserCallsAdapter(requireContext(), viewModel)

        binding.root.setHasTransientState(true)
        binding.callRecyclerView.adapter = adapter
        binding.lifecycleOwner = this
        val linearLayoutManager = LinearLayoutManager(requireContext())
        binding.callRecyclerView.layoutManager = linearLayoutManager
        val userCalls: LiveData<List<UserCalls>> = callsRepository.getUserCalls
        userCalls.observe(viewLifecycleOwner, Observer {
            adapter.setAdapterData(it)
        })

        return binding.root
    }

//    override fun onResume() {
//        val userCalls: LiveData<List<UserCalls>> = callsRepository.getUserCalls
//        if (userCalls.value != null) {
//            Log.i("+++","on resume if")
////            adapter.submitList(userCalls.value!!)
//            adapter.setAdapterData(userCalls.value!!)
//        }
//        super.onResume()
//    }
}
