package com.example.dynamicmessenger.userChatRoom.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dynamicmessenger.R
import com.example.dynamicmessenger.network.authorization.models.ChatRoom

class ChatRoomAdapter(val context: Context, private val myID: String) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var data = listOf<ChatRoom>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }


    override fun getItemCount(): Int {
        return data.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (data[position].sender.id == myID) {
            1
        } else {
            0
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 0) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.message_item_view, parent, false)
            ChatRoomViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.message_sender_item_view, parent, false)
            ChatRoomSenderViewHolder(view)
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) =
        when (holder.itemViewType) {
            1 -> (holder as ChatRoomSenderViewHolder).bind(position)
            0 -> (holder as ChatRoomViewHolder).bind(position)
            else -> throw IllegalArgumentException()
        }

    inner class ChatRoomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val chatRoom: ChatRoom? = null
        private val message: TextView = itemView.findViewById(R.id.receiverTextView)
        internal fun bind(position: Int) {
            message.text = data[position].text
        }
    }

    inner class ChatRoomSenderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val message: TextView = itemView.findViewById(R.id.senderTextView)
        internal fun bind(position: Int) {
            message.text = data[position].text
        }
    }

}