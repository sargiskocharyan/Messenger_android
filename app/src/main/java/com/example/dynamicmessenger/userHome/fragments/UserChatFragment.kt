package com.example.dynamicmessenger.userHome.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dynamicmessenger.R
import com.example.dynamicmessenger.databinding.FragmentUserChatBinding
import com.example.dynamicmessenger.network.authorization.models.Chat
import com.example.dynamicmessenger.userDataController.SharedPreferencesManager
import com.example.dynamicmessenger.userHome.adapters.UserChatsAdapter
import com.example.dynamicmessenger.userHome.viewModels.UserChatViewModel
import java.text.SimpleDateFormat
import java.util.*


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

//    @SuppressLint("SimpleDateFormat")
    private fun updateRecyclerView(adapter: UserChatsAdapter) {
        viewModel.getUserChatsFromNetwork(requireContext()) {
//            it.forEach {chat ->
//                val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
//                val date: Date = format.parse(chat.message?.createdAt)!!
//                Log.i("+++", date.time.toString())
//                chat.message!!.createdAt = date.toString()
//            }
//            it.sortedBy {chat ->
//                chat.message!!.createdAt
//            }

            adapter.data = it
            adapter.notifyDataSetChanged()
        }
    }
}
