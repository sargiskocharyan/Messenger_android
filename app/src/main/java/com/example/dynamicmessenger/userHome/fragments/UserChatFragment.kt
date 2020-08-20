package com.example.dynamicmessenger.userHome.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.dynamicmessenger.R
import com.example.dynamicmessenger.activitys.HomeActivity
import com.example.dynamicmessenger.common.MyFragments
import com.example.dynamicmessenger.common.SharedConfigs
import com.example.dynamicmessenger.databinding.FragmentChatRoomBinding
import com.example.dynamicmessenger.databinding.FragmentUserChatBinding
import com.example.dynamicmessenger.dialogs.ContactsSearchDialog
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
    private lateinit var adapter: UserChatsAdapter

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

        SharedConfigs.currentFragment.value = MyFragments.CHATS
        adapter = UserChatsAdapter(requireContext())
        binding.root.setHasTransientState(true)

        binding.chatsRecyclerView.adapter = adapter
        val linearLayoutManager = LinearLayoutManager(requireContext())
        binding.chatsRecyclerView.layoutManager = linearLayoutManager

        refreshRecyclerView(adapter)
        binding.userChatSwipeRefreshLayout.setOnRefreshListener {
            refreshRecyclerView(adapter)
        }

        HomeActivity.opponentUser = null
        HomeActivity.isAddContacts = null

        //Socket
        SocketManager.addChatFragment(this)

        return binding.root
    }

    //for show toolbar menu
    override fun onPrepareOptionsMenu(menu: Menu) {
        val inflater: MenuInflater = requireActivity().menuInflater
        inflater.inflate(R.menu.plus_top_bar, menu)
        super.onPrepareOptionsMenu(menu)
    }

    override fun onDestroy() {
        super.onDestroy()
        SocketManager.removeChatFragment()
    }

    private fun getUserChats(adapter: UserChatsAdapter, swipeRefreshLayout: SwipeRefreshLayout? = null) {
        SharedConfigs.userRepository.getUserChats(swipeRefreshLayout).observe(viewLifecycleOwner, Observer {
            if (it != null) {
                val list = it.sortedWith(compareBy { chat -> chat.message }).reversed()
                adapter.submitList(list)
                scrollToTop(binding)
            } else {
                Toast.makeText(requireContext(), "try again", Toast.LENGTH_SHORT).show()
            }
            swipeRefreshLayout?.isRefreshing = false
        })
    }

    fun updateRecyclerView() {
        getUserChats(adapter)
    }

    private fun refreshRecyclerView(adapter: UserChatsAdapter) {
        getUserChats(adapter, binding.userChatSwipeRefreshLayout)
    }

    private fun configureTopNavBar(toolbar: Toolbar) {
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        toolbar.setOnMenuItemClickListener {
            val selectedFragment = UserContactsFragment()
            SharedConfigs.lastFragment = MyFragments.CHATS
            activity?.supportFragmentManager
                ?.beginTransaction()
                ?.replace(R.id.fragmentContainer, selectedFragment)
                ?.addToBackStack(null)
                ?.commit()

            return@setOnMenuItemClickListener true
        }
    }

    private fun scrollToTop(binding: FragmentUserChatBinding) {
        binding.chatsRecyclerView.scrollToPosition(0)
    }
}
