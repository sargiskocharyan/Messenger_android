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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dynamicmessenger.R
import com.example.dynamicmessenger.databinding.FragmentUserChatBinding
import com.example.dynamicmessenger.network.chatRooms.SocketManager
import com.example.dynamicmessenger.userDataController.database.*
import com.example.dynamicmessenger.userHome.adapters.UserChatsAdapter
import com.example.dynamicmessenger.userHome.viewModels.UserChatViewModel
import com.github.nkzawa.socketio.client.Socket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job


class UserChatFragment : Fragment() {
    private lateinit var viewModel: UserChatViewModel
    private lateinit var binding : FragmentUserChatBinding
    private lateinit var socketManager: SocketManager
    private lateinit var mSocket: Socket
    private var activityJob = Job()

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

        val adapter = UserChatsAdapter(requireContext(), activityJob)
        updateRecyclerViewFromDatabase(adapter)

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

    override fun onDestroy() {
        super.onDestroy()
        activityJob.cancel()
    }

    private fun updateRecyclerView(adapter: UserChatsAdapter) {
        binding.userChatSwipeRefreshLayout.isRefreshing = true
        viewModel.getUserChatsFromNetwork(requireContext(), binding.userChatSwipeRefreshLayout) {
            val list = it.sortedWith(compareBy { chat -> chat.message }).reversed()
            adapter.setAdapterData(list)
            adapter.submitList(list)
            binding.userChatSwipeRefreshLayout.isRefreshing = false
        }
    }

    private fun updateRecyclerViewFromDatabase(adapter: UserChatsAdapter) {
        viewModel.getUserChatsFromNetwork(requireContext(), binding.userChatSwipeRefreshLayout) {
            val list = it.sortedWith(compareBy { chat -> chat.message }).reversed()
            adapter.setAdapterDataNotify(list)
        }
    }
}
