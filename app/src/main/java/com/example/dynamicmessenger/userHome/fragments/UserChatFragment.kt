package com.example.dynamicmessenger.userHome.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dynamicmessenger.R
import com.example.dynamicmessenger.activitys.HomeActivity
import com.example.dynamicmessenger.databinding.FragmentChatRoomBinding
import com.example.dynamicmessenger.databinding.FragmentUserChatBinding
import com.example.dynamicmessenger.network.chatRooms.SocketManager
import com.example.dynamicmessenger.userChatRoom.adapters.ChatRoomAdapter
import com.example.dynamicmessenger.userHome.adapters.UserChatsAdapter
import com.example.dynamicmessenger.userHome.viewModels.UserChatViewModel
import com.github.nkzawa.socketio.client.Socket
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job


class UserChatFragment : Fragment() {
    private lateinit var viewModel: UserChatViewModel
    private lateinit var binding: FragmentUserChatBinding
    private lateinit var socketManager: SocketManager
    private lateinit var mSocket: Socket
    private var activityJob = Job()

    @SuppressLint("ResourceType")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserChatBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this).get(UserChatViewModel::class.java)

        //toolbar
        setHasOptionsMenu(true)
        val toolbar: Toolbar = binding.userChatToolbar
        configureTopNavBar(toolbar)

        //bottom navigation
        val bottomNavBar: BottomNavigationView = requireActivity().findViewById(R.id.bottomNavigationView)
        bottomNavBar.visibility = View.VISIBLE

        val adapter = UserChatsAdapter(requireContext(), activityJob)
        updateRecyclerViewFromDatabase(adapter)

        binding.root.setHasTransientState(true)
        binding.userChatSwipeRefreshLayout.setOnRefreshListener {
            updateRecyclerView(adapter)
        }
        binding.chatsRecyclerView.adapter = adapter
        val linearLayoutManager = LinearLayoutManager(requireContext())
        binding.chatsRecyclerView.layoutManager = linearLayoutManager
        adapter.setAdapterDataNotify(viewModel.getUserChats().sortedWith(compareBy { chat -> chat.message }).reversed())

        HomeActivity.opponentUser = null
        HomeActivity.isAddContacts = null

        //Socket
        socketManager = SocketManager
        try {
            mSocket = socketManager.getSocketInstance()!!
        } catch (e: Exception) {
            Log.i("+++", "UserChatFragment socket $e")
        }

//        mSocket.connect()
        mSocket.on("message", socketManager.onMessageForAllChats(Activity()) {
            try {
                if (it) updateRecyclerView(adapter)
            } catch (e: Exception) {
                Log.i("+++", "UserChatFragment socket event message $e")
            }
        })

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        HomeActivity.receiverChatInfo = null
        activityJob.cancel()
    }

    private fun updateRecyclerView(adapter: UserChatsAdapter) {
        binding.userChatSwipeRefreshLayout.isRefreshing = true
        viewModel.getUserChatsFromNetwork(requireContext(), binding.userChatSwipeRefreshLayout) {
            val list = it.sortedWith(compareBy { chat -> chat.message }).reversed()
            adapter.submitList(list)
            binding.userChatSwipeRefreshLayout.isRefreshing = false
            scrollToTop(binding)
        }
    }

    private fun updateRecyclerViewFromDatabase(adapter: UserChatsAdapter) {
        binding.userChatSwipeRefreshLayout.isRefreshing = true
        viewModel.getUserChatsFromNetwork(requireContext(), binding.userChatSwipeRefreshLayout) {
            val list = it.sortedWith(compareBy { chat -> chat.message }).reversed()
            adapter.submitList(list)
            binding.userChatSwipeRefreshLayout.isRefreshing = false
            scrollToTop(binding)
        }
    }

    private fun configureTopNavBar(toolbar: Toolbar) {
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        toolbar.elevation = 10.0F
        toolbar.setOnMenuItemClickListener {
            Toast.makeText(requireContext(), "sexmir", Toast.LENGTH_SHORT).show()
            return@setOnMenuItemClickListener true
        }
    }

    private fun scrollToTop(binding: FragmentUserChatBinding) {
        binding.chatsRecyclerView.scrollToPosition(0)
    }
}
