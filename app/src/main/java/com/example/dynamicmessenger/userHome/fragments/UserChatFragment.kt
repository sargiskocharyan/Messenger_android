package com.example.dynamicmessenger.userHome.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dynamicmessenger.R
import com.example.dynamicmessenger.databinding.FragmentUserChatBinding
import com.example.dynamicmessenger.userHome.adapters.UserChatsAdapter
import com.example.dynamicmessenger.userHome.viewModels.UserChatViewModel


class UserChatFragment : Fragment() {
    private lateinit var viewModel: UserChatViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding : FragmentUserChatBinding =
            DataBindingUtil.inflate(inflater,
                R.layout.fragment_user_chat,
                container,false)
        viewModel = ViewModelProvider(this).get(UserChatViewModel::class.java)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        val adapter = UserChatsAdapter(requireContext())
        updateRecyclerView(adapter)

        binding.root.setHasTransientState(true)
        binding.chatsRecyclerView.adapter = adapter
        val linearLayoutManager = LinearLayoutManager(requireContext())
        binding.chatsRecyclerView.layoutManager = linearLayoutManager

        return binding.root
    }

    private fun updateRecyclerView(adapter: UserChatsAdapter) {
        viewModel.getUserChatsFromNetwork(requireContext()) {
            adapter.data = it.sortedWith(compareBy { chat -> chat.message }).reversed()
            adapter.notifyDataSetChanged()
        }
    }
}
