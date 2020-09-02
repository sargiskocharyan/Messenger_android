package com.example.dynamicmessenger.userHome.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.dynamicmessenger.R
import com.example.dynamicmessenger.activitys.HomeActivity
import com.example.dynamicmessenger.common.MyFragments
import com.example.dynamicmessenger.common.SharedConfigs
import com.example.dynamicmessenger.databinding.FragmentUserChatBinding
import com.example.dynamicmessenger.network.authorization.models.Chat
import com.example.dynamicmessenger.network.authorization.models.MessageStatus
import com.example.dynamicmessenger.network.chatRooms.SocketManager
import com.example.dynamicmessenger.userHome.adapters.UserChatsAdapter
import com.example.dynamicmessenger.userHome.viewModels.UserChatViewModel
import com.example.dynamicmessenger.utils.toDate


class UserChatFragment : Fragment() {
    private lateinit var viewModel: UserChatViewModel
    private lateinit var binding: FragmentUserChatBinding
    private lateinit var adapter: UserChatsAdapter

    @SuppressLint("ResourceType")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(UserChatViewModel::class.java)
        adapter = UserChatsAdapter(requireContext())
        binding = FragmentUserChatBinding.inflate(layoutInflater)
        binding.root.setHasTransientState(true)
        binding.chatsRecyclerView.adapter = adapter
        binding.chatsRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        //toolbar
        setHasOptionsMenu(true)
        configureTopNavBar(binding.userChatToolbar)

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

    override fun onResume() {
        super.onResume()
        SharedConfigs.currentFragment.value = MyFragments.CHATS
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
                scrollToTop()
                list.forEach { chat ->
                    chat.statuses.let { messageStatus ->
                        if (messageStatus[0].userId == SharedConfigs.signedUser?._id ?: false) {
                            sendReceiveMessage(chat, messageStatus[0])
                        } else {
                            sendReceiveMessage(chat, messageStatus[1])
                        }
                    }
                }
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
            requireActivity().supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragmentContainer, selectedFragment)
                .addToBackStack(null)
                .commit()

            return@setOnMenuItemClickListener true
        }
    }

    private fun scrollToTop() {
        binding.chatsRecyclerView.scrollToPosition(0)
    }

    private fun sendReceiveMessage(chat: Chat, status: MessageStatus) {
        status.receivedMessageDate.toDate()?.let { receivedMessageDate ->
            chat.message?.createdAt.toDate()?.let { createdAt ->
                if (receivedMessageDate < createdAt) {
                    chat.message?.let { message ->
                        if (message.senderId != SharedConfigs.signedUser?._id) {
                            SocketManager.messageReceived(message.senderId, message._id)
                            Log.i("+++", "message received ${message}")
                        }
                    }
                }
            }
        }
    }
}
