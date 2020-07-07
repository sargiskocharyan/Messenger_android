package com.example.dynamicmessenger.userChatRoom.fragments

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dynamicmessenger.R
import com.example.dynamicmessenger.common.SharedConfigs
import com.example.dynamicmessenger.databinding.FragmentChatRoomBinding
import com.example.dynamicmessenger.network.authorization.LoadAvatarApi
import com.example.dynamicmessenger.network.chatRooms.SocketManager
import com.example.dynamicmessenger.userChatRoom.adapters.ChatRoomAdapter
import com.example.dynamicmessenger.userChatRoom.adapters.ChatRoomDiffUtilCallback
import com.example.dynamicmessenger.userChatRoom.viewModels.ChatRoomViewModel
import com.example.dynamicmessenger.userDataController.SharedPreferencesManager
import com.github.nkzawa.socketio.client.Socket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


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
        val receiverID = SharedPreferencesManager.getReceiverID(requireContext())
        val myID = SharedConfigs.signedUser!!._id
        val receiverAvatar = SharedPreferencesManager.getReceiverAvatarUrl(requireContext())
        adapter = ChatRoomAdapter(requireContext(), myID)
        val linearLayoutManager = LinearLayoutManager(requireContext())
        binding.chatRecyclerView.adapter = adapter

//        (activity as AppCompatActivity).supportActionBar?.title = "Title"
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
}