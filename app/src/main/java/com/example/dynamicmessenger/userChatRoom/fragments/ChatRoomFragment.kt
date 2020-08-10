package com.example.dynamicmessenger.userChatRoom.fragments

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dynamicmessenger.R
import com.example.dynamicmessenger.activitys.HomeActivity
import com.example.dynamicmessenger.common.SharedConfigs
import com.example.dynamicmessenger.databinding.FragmentChatRoomBinding
import com.example.dynamicmessenger.network.chatRooms.SocketManager
import com.example.dynamicmessenger.userChatRoom.adapters.ChatRoomAdapter
import com.example.dynamicmessenger.userChatRoom.viewModels.ChatRoomViewModel
import com.github.nkzawa.socketio.client.Socket
import com.google.android.material.bottomnavigation.BottomNavigationView


class ChatRoomFragment : Fragment() {
    private lateinit var viewModel: ChatRoomViewModel
    private lateinit var mSocket: Socket
    private lateinit var binding: FragmentChatRoomBinding
    private lateinit var adapter: ChatRoomAdapter
    private lateinit var socketManager: SocketManager
    private var scrollUpWhenKeyboardOpened = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChatRoomBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this).get(ChatRoomViewModel::class.java)
        val myID = SharedConfigs.signedUser!!._id
        val receiverID = HomeActivity.receiverID!!
        adapter = ChatRoomAdapter(requireContext(), myID)
        val linearLayoutManager = LinearLayoutManager(requireContext())
        binding.chatRecyclerView.adapter = adapter

        val bottomNavBar: BottomNavigationView =
            requireActivity().findViewById(R.id.bottomNavigationView)
        bottomNavBar.visibility = View.GONE

        //Toolbar
        setHasOptionsMenu(true)
        val toolbar: Toolbar = binding.chatRoomToolbar
        configureTopNavBar(toolbar)

        SharedConfigs.userRepository.getUserInformation(receiverID).observe(viewLifecycleOwner, Observer {user ->
            if (user != null) {
                HomeActivity.opponentUser = user
                binding.userChatToolbarTitle.text = user.username ?: ""
                SharedConfigs.userRepository.getAvatar(user.avatarURL).observe(viewLifecycleOwner, Observer {bitmap ->
                    adapter.receiverImage = bitmap
                })
            }
        })

//        viewModel.getOpponentInfoFromNetwork(receiverID)

        binding.chatRecyclerView.layoutManager = linearLayoutManager

        binding.chatRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                scrollUpWhenKeyboardOpened = if (linearLayoutManager.findLastVisibleItemPosition() != adapter.itemCount - 1) {
                    Log.i("+++", "scroll state changed ${linearLayoutManager.findLastVisibleItemPosition()}")
                    false
                } else {
                    true
                }
            }
        })

        updateRecyclerView(receiverID)
        binding.root.setHasTransientState(true)

        //socket
        socketManager = SocketManager

        try {
            mSocket = socketManager.getSocketInstance()!!
        } catch (e: Exception) {
            Log.i("+++", "ChatRoomFragment Socket $e")
        }
//        mSocket.connect()
        mSocket.on("message", socketManager.onMessage(adapter, receiverID, activity))
        binding.sendMessageButton.setOnClickListener {
            socketManager.sendMessage(receiverID, binding.sendMessageEditText)
        }

        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                scrollToBottom(binding, adapter)
            }
        })


        return binding.root
    }

    //for show toolbar menu
    override fun onPrepareOptionsMenu(menu: Menu) {
        val inflater: MenuInflater = requireActivity().menuInflater
        inflater.inflate(R.menu.chat_top_bar, menu)
        super.onPrepareOptionsMenu(menu)
    }

//    override fun onDestroyView() {
//        super.onDestroyView()
//        HomeActivity.isAddContacts = false
//    }

    private fun updateRecyclerView(receiverID: String) {
        viewModel.getMessagesFromNetwork(requireContext(), receiverID) {
            adapter.submitList(it)
            scrollToBottom(binding, adapter)
        }
    }

    private fun scrollToBottom(binding: FragmentChatRoomBinding, adapter: ChatRoomAdapter) {
        binding.chatRecyclerView.scrollToPosition(adapter.itemCount - 1)
    }

    private fun configureTopNavBar(toolbar: Toolbar) {
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
        toolbar.setOnMenuItemClickListener {
            activity?.supportFragmentManager
                ?.beginTransaction()
                ?.replace(R.id.fragmentContainer, OpponentInformationFragment())
                ?.addToBackStack(null)
                ?.commit()
            return@setOnMenuItemClickListener true
        }
    }
}