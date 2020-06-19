package com.example.dynamicmessenger.userChatRoom.fragments

import android.database.DataSetObserver
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.RelativeLayout
import androidx.core.view.size
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dynamicmessenger.R
import com.example.dynamicmessenger.databinding.FragmentChatRoomBinding
import com.example.dynamicmessenger.network.authorization.models.ChatRoom
import com.example.dynamicmessenger.network.authorization.models.Message
import com.example.dynamicmessenger.network.chatRooms.SocketManager
import com.example.dynamicmessenger.userChatRoom.adapters.ChatRoomAdapter
import com.example.dynamicmessenger.userChatRoom.viewModels.ChatRoomViewModel
import com.example.dynamicmessenger.userDataController.SharedPreferencesManager
import com.github.nkzawa.emitter.Emitter
import com.github.nkzawa.socketio.client.Socket
import com.google.gson.Gson
import kotlinx.coroutines.awaitAll
import org.json.JSONException
import org.json.JSONObject


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
            mSocket = socketManager.getSocketInstance()
        } catch (e: Exception){
            Log.i("+++", e.toString())
        }
        mSocket.on("message", socketManager.onMessage(adapter, receiverID, activity))
        mSocket.connect()
        mSocket.emit("messageTest" , "Davona")
        binding.sendMessageButton.setOnClickListener {
            socketManager.sendMessage(receiverID, binding.sendMessageEditText)
        }

//        binding.sendMessageEditText.setOnFocusChangeListener { v, hasFocus ->
//            if (hasFocus) {
//                binding.messagesRelativeLayout.setH = RelativeLayout.LayoutParams.MATCH_PARENT
//            }
//        }

        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver(){
            override fun onChanged() {
                super.onChanged()
//                Log.i("+++DataObserver", adapter.itemCount.toString() + " state " + firstVisibleItemPosition)
//                if (firstVisibleItemPosition == adapter.itemCount - 2) {
//                    Log.i("+++ifDataObserver", adapter.itemCount.toString() + " state " + firstVisibleItemPosition)
                scrollToBottom(binding, adapter)
//                }
            }
        })

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mSocket.off("message", socketManager.onMessage(adapter, "", activity))
        mSocket.disconnect()

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