package com.example.dynamicmessenger.userHome.fragments

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dynamicmessenger.R
import com.example.dynamicmessenger.databinding.FragmentUserChatBinding
import com.example.dynamicmessenger.network.chatRooms.SocketManager
import com.example.dynamicmessenger.userHome.adapters.UserChatDiffUtilCallback
import com.example.dynamicmessenger.userHome.adapters.UserChatsAdapter
import com.example.dynamicmessenger.userHome.viewModels.UserChatViewModel
import com.github.nkzawa.socketio.client.Socket


class UserChatFragment : Fragment() {
    private lateinit var viewModel: UserChatViewModel
    private lateinit var binding : FragmentUserChatBinding
    private lateinit var socketManager: SocketManager
    private lateinit var mSocket: Socket

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater,
                R.layout.fragment_user_chat,
                container,false)
        viewModel = ViewModelProvider(this).get(UserChatViewModel::class.java)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        val adapter = UserChatsAdapter(requireContext())
        updateRecyclerView(adapter)

        binding.root.setHasTransientState(true)
        binding.userChatSwipeRefreshLayout.setOnRefreshListener {
            updateRecyclerView(adapter)
        }
        binding.chatsRecyclerView.adapter = adapter
        val linearLayoutManager = LinearLayoutManager(requireContext())
        binding.chatsRecyclerView.layoutManager = linearLayoutManager

        socketManager = SocketManager(requireContext())
        try {
            mSocket = socketManager.getSocketInstance()!!
        } catch (e: Exception){
            Log.i("+++", e.toString())
        }

        mSocket.connect()
        mSocket.on("message", socketManager.onMessageForAllChats(Activity()){
            try {
                if (it) updateRecyclerView(adapter)
            } catch (e: Exception){
                Log.i("+++", e.toString())
            }
        })

        return binding.root
    }

    private fun updateRecyclerView(adapter: UserChatsAdapter) {
        binding.userChatSwipeRefreshLayout.isRefreshing = true
        viewModel.getUserChatsFromNetwork(requireContext(), binding.userChatSwipeRefreshLayout) {
            val userChatDiffUtilCallback = UserChatDiffUtilCallback(adapter.data, it.sortedWith(compareBy { chat -> chat.message }).reversed())
            val authorDiffResult = DiffUtil.calculateDiff(userChatDiffUtilCallback)
            adapter.data = it.sortedWith(compareBy { chat -> chat.message }).reversed()
            binding.userChatSwipeRefreshLayout.isRefreshing = false
            authorDiffResult.dispatchUpdatesTo(adapter)
        }
    }
}
