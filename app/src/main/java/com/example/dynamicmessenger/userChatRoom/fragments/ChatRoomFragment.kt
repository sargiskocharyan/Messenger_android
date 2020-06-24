package com.example.dynamicmessenger.userChatRoom.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.Window
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dynamicmessenger.R
import com.example.dynamicmessenger.databinding.FragmentChatRoomBinding
import com.example.dynamicmessenger.network.chatRooms.SocketManager
import com.example.dynamicmessenger.userChatRoom.adapters.ChatRoomAdapter
import com.example.dynamicmessenger.userChatRoom.viewModels.ChatRoomViewModel
import com.example.dynamicmessenger.userDataController.SharedPreferencesManager
import com.github.nkzawa.socketio.client.Socket


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
        val myID = SharedPreferencesManager.loadUserObject(requireContext())!!._id
        adapter = ChatRoomAdapter(requireContext(), myID)
        val linearLayoutManager = LinearLayoutManager(requireContext())
//        linearLayoutManager.stackFromEnd = true
        val firstVisibleItemPosition =  linearLayoutManager.findFirstVisibleItemPosition()
        binding.chatRecyclerView.layoutManager = linearLayoutManager

        updateRecyclerView(receiverID)
        binding.root.setHasTransientState(true)
        Log.i("+++itemCount", adapter.itemCount.toString())
        binding.chatRecyclerView.adapter = adapter
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
            Log.i("+++", "send message $receiverID ${binding.sendMessageEditText.text}")
            socketManager.sendMessage(receiverID, binding.sendMessageEditText)
        }

/*
//        binding.sendMessageEditText.addTextChangedListener(object : TextWatcher {
//            @SuppressLint("ResourceAsColor")
//            override fun afterTextChanged(s: Editable?) {
//                scrollToBottom(binding, adapter)
//            }
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
////                scrollToBottom(binding, adapter)
//            }
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
////                scrollToBottom(binding, adapter)
//            }
//        })

        binding.sendMessageEditText.setOnFocusChangeListener { _, hasFocus ->
            Log.i("+++", hasFocus.toString())
            if (hasFocus) {
                scrollToBottom(binding, adapter)
            }
        }
        var mLastContentHeight = 0
        val keyboardLayoutListener = OnGlobalLayoutListener {
            val currentContentHeight: Int = binding.messagesRelativeLayout.height
            if (mLastContentHeight > currentContentHeight + 100) {
                mLastContentHeight = currentContentHeight
            } else if (currentContentHeight > mLastContentHeight + 100) {
                mLastContentHeight = currentContentHeight
            }
        }


//        binding.sendMessageEditText.setOnClickListener {
//            scrollToBottom(binding, adapter)
//        }
*/ 

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

    }

    private fun updateRecyclerView(receiverID: String) {
        viewModel.getMessagesFromNetwork(requireContext(), receiverID) {
            adapter.data = it
            Log.i("+++itermcount", adapter.itemCount.toString())
            scrollToBottom(binding, adapter)
            adapter.notifyDataSetChanged()
        }
    }

    private fun scrollToBottom(binding : FragmentChatRoomBinding, adapter: ChatRoomAdapter) {
        binding.chatRecyclerView.scrollToPosition(adapter.itemCount - 1)
    }

/*
//    private fun onMessage(adapter: ChatRoomAdapter): Emitter.Listener {
//        return Emitter.Listener { args ->
//            activity?.runOnUiThread(Runnable {
//                val data = args[0] as JSONObject
//                val gson = Gson()
//                val gsonMessage = gson.fromJson(data.toString(), Message::class.java)
//                val message = ChatRoom(gsonMessage.sender,gsonMessage.text)
//                try {
//                    Log.i("+++", gsonMessage.toString())
//                    Log.i("+++", data.toString())
////                    if (adapter.data.)
//                    adapter.data += message
//                    adapter.notifyDataSetChanged()
//                } catch (e: JSONException) {
//                    Log.i("+++", e.toString())
//                    return@Runnable
//                }
//            })
//        }
//    }
//    private fun sendMessage(receiverID: String, text: EditText) {
//        mSocket.emit("sendMessage" , text.text.toString() , receiverID)
//        text.text.clear()
//    }
 */
}