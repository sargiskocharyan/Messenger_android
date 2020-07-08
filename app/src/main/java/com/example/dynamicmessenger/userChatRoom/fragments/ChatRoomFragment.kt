package com.example.dynamicmessenger.userChatRoom.fragments

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getColor
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dynamicmessenger.R
import com.example.dynamicmessenger.common.SharedConfigs
import com.example.dynamicmessenger.databinding.FragmentChatRoomBinding
import com.example.dynamicmessenger.network.chatRooms.SocketManager
import com.example.dynamicmessenger.userChatRoom.adapters.ChatRoomAdapter
import com.example.dynamicmessenger.userChatRoom.adapters.ChatRoomDiffUtilCallback
import com.example.dynamicmessenger.userChatRoom.viewModels.ChatRoomViewModel
import com.example.dynamicmessenger.userDataController.SharedPreferencesManager
import com.github.nkzawa.socketio.client.Socket
import com.google.android.material.bottomnavigation.BottomNavigationView


class ChatRoomFragment : Fragment() {
    private lateinit var viewModel: ChatRoomViewModel
    private lateinit var mSocket: Socket
    private lateinit var binding : FragmentChatRoomBinding
    private lateinit var adapter: ChatRoomAdapter
    private lateinit var socketManager: SocketManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater,
                R.layout.fragment_chat_room,
                container, false)
        viewModel = ViewModelProvider(this).get(ChatRoomViewModel::class.java)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        val myID = SharedConfigs.signedUser!!._id
        val receiverID = SharedPreferencesManager.getReceiverID(requireContext())
        val receiverAvatar = SharedPreferencesManager.getReceiverAvatarUrl(requireContext())
        adapter = ChatRoomAdapter(requireContext(), myID)
        val linearLayoutManager = LinearLayoutManager(requireContext())
        binding.chatRecyclerView.adapter = adapter

        val bottomNavBar: BottomNavigationView = requireActivity().findViewById(R.id.bottomNavigationView)
        bottomNavBar.visibility = View.GONE
        val topNavBar = (activity as AppCompatActivity).supportActionBar
        configureTopNavBar(topNavBar, "Title")

        viewModel.getAvatar(receiverAvatar){
            adapter.receiverImage = it
        }

//        linearLayoutManager.stackFromEnd = true
        val firstVisibleItemPosition =  linearLayoutManager.findFirstVisibleItemPosition()
        binding.chatRecyclerView.layoutManager = linearLayoutManager

        updateRecyclerView(receiverID)
        binding.root.setHasTransientState(true)

        //socket
        socketManager = SocketManager(requireContext())

        try {
            mSocket = socketManager.getSocketInstance()!!
        } catch (e: Exception){
            Log.i("+++", e.toString())
        }
        mSocket.connect()
        mSocket.on("message", socketManager.onMessage(adapter, receiverID, activity))
        binding.sendMessageButton.setOnClickListener {
            socketManager.sendMessage(receiverID, binding.sendMessageEditText)
        }

        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver(){
            override fun onChanged() {
                super.onChanged()
                scrollToBottom(binding, adapter)
            }
        })

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        val inflater: MenuInflater = requireActivity().menuInflater
        inflater.inflate(R.menu.chat_top_bar, menu)
        return
    }

    override fun onDestroyView() {
        super.onDestroyView()
        socketManager.closeSocket()
        SharedPreferencesManager.setReceiverAvatarUrl(requireContext(), "")
    }

    private fun updateRecyclerView(receiverID: String) {
        viewModel.getMessagesFromNetwork(requireContext(), receiverID) {
            val userChatDiffUtilCallback = ChatRoomDiffUtilCallback(adapter.data, it)
            val authorDiffResult = DiffUtil.calculateDiff(userChatDiffUtilCallback)
            adapter.data = it
            scrollToBottom(binding, adapter)
            authorDiffResult.dispatchUpdatesTo(adapter)
        }
    }

    private fun scrollToBottom(binding : FragmentChatRoomBinding, adapter: ChatRoomAdapter) {
        binding.chatRecyclerView.scrollToPosition(adapter.itemCount - 1)
    }

    private fun configureTopNavBar(topNavBar: ActionBar?, title: String) {
        topNavBar?.show()
//        topNavBar?.setHomeButtonEnabled(true)
        topNavBar?.setDisplayHomeAsUpEnabled(true)
        topNavBar?.title = title
        topNavBar?.setBackgroundDrawable(ColorDrawable(getColor(requireContext(), R.color.white)))
    }
}