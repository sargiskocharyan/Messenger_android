package com.example.dynamicmessenger.userChatRoom.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.dynamicmessenger.R
import com.example.dynamicmessenger.network.authorization.models.ChatRoom
import com.example.dynamicmessenger.utils.Utils
import java.text.SimpleDateFormat
import java.util.*

class ChatRoomAdapter(val context: Context, private val myID: String) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        private const val SENDER_TEXT = 1
        private const val SENDER_CALL = 3
        private const val RECEIVER_TEXT = 0
        private const val RECEIVER_CALL = 2
    }

    var data = mutableListOf<ChatRoom>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var receiverImage: Bitmap? = null

    fun submitList(newList: List<ChatRoom>) {
        val userChatDiffUtilCallback = ChatRoomDiffUtilCallback(data, newList)
        val authorDiffResult = DiffUtil.calculateDiff(userChatDiffUtilCallback)
        authorDiffResult.dispatchUpdatesTo(this)
        data.clear()
        data.addAll(newList)
    }


    override fun getItemCount(): Int {
        return data.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (data[position].senderId == myID && data[position].type == "text") {
            SENDER_TEXT
        } else if (data[position].senderId == myID && data[position].type == "call") {
            SENDER_CALL
        } else if (data[position].senderId != myID && data[position].type == "text") {
            RECEIVER_TEXT
        } else {
            RECEIVER_CALL
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            RECEIVER_TEXT -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_view_message_receiver, parent, false)
                ChatRoomViewHolder(view)
            }
            SENDER_TEXT -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_view_message_sender, parent, false)
                ChatRoomSenderViewHolder(view)
            }
            RECEIVER_CALL -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_view_message_receiver_call, parent, false)
                ChatRoomReceiverCallViewHolder(view)
            }
            SENDER_CALL -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_view_message_sender_call, parent, false)
                ChatRoomSenderCallViewHolder(view)
            }
            else -> throw IllegalArgumentException()
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) =
        when (holder.itemViewType) {
            SENDER_CALL -> (holder as ChatRoomSenderCallViewHolder).bind(position)
            RECEIVER_CALL -> (holder as ChatRoomReceiverCallViewHolder).bind(position)
            SENDER_TEXT -> (holder as ChatRoomSenderViewHolder).bind(position)
            RECEIVER_TEXT -> (holder as ChatRoomViewHolder).bind(position)
            else -> throw IllegalArgumentException()
        }

    inner class ChatRoomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val chatRoom: ChatRoom? = null
        private val message: TextView = itemView.findViewById(R.id.receiverTextView)
        private val receiverImageView: ImageView = itemView.findViewById(R.id.receiverImageView)
        @SuppressLint("SetTextI18n")
        internal fun bind(position: Int) {
            message.text = data[position].text
            if (receiverImage != null) {
                receiverImageView.setImageBitmap(receiverImage)
            } else  {
                receiverImageView.setImageResource(R.drawable.ic_user_image)
            }
        }
    }

    inner class ChatRoomSenderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val message: TextView = itemView.findViewById(R.id.senderTextView)
        internal fun bind(position: Int) {
            message.text = data[position].text
        }
    }

    inner class ChatRoomSenderCallViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val senderCallingStateTextView: TextView = itemView.findViewById(R.id.senderCallingStateTextView)
        private val callDurationTextView: TextView = itemView.findViewById(R.id.senderCallDurationTextView)
        private val callTimeTextView: TextView = itemView.findViewById(R.id.senderCallTimeTextView)
        init {
            itemView.setOnClickListener {
                Toast.makeText(context, "zang", Toast.LENGTH_SHORT).show()
            }
        }
        @SuppressLint("SetTextI18n")
        internal fun bind(position: Int) {
            if (data[position].call?.duration != null) {
                callDurationTextView.text = "${data[position].call?.duration?.toInt()} sec."
            } else {
                callDurationTextView.text = "0 sec."
            }
            when (data[position].call?.status) {
                "accepted" -> { senderCallingStateTextView.text = "Outgoing call" }
                "cancelled" -> { senderCallingStateTextView.text = "Cancelled call" }
                "missed" -> { senderCallingStateTextView.text = "Missed call" }
            }
            if (data[position].call?.callSuggestTime != null) {
                callTimeTextView.text = Utils.dateConverter(data[position].call?.callSuggestTime!!)
            }
        }
    }

    inner class ChatRoomReceiverCallViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val receiverCallingStateTextView: TextView = itemView.findViewById(R.id.receiverCallingStateTextView)
        private val receiverCallDurationTextView: TextView = itemView.findViewById(R.id.receiverCallDurationTextView)
        private val receiverCallTimeTextView: TextView = itemView.findViewById(R.id.receiverCallTimeTextView)
        private val receiverCallImageView: ImageView = itemView.findViewById(R.id.receiverCallImageView)
        init {
            itemView.setOnClickListener {
                Toast.makeText(context, "zang", Toast.LENGTH_SHORT).show()
            }
        }
        @SuppressLint("SetTextI18n")
        internal fun bind(position: Int) {
            if (receiverImage != null) {
                receiverCallImageView.setImageBitmap(receiverImage)
            } else  {
                receiverCallImageView.setImageResource(R.drawable.ic_user_image)
            }
            if (data[position].call?.duration != null) {
                receiverCallDurationTextView.text = "${data[position].call?.duration?.toInt()} sec."
            } else {
                receiverCallDurationTextView.text = "0 sec."
            }
            when (data[position].call?.status) {
                "accepted" -> { receiverCallingStateTextView.text = "Incoming call" }
                "cancelled" -> { receiverCallingStateTextView.text = "Cancelled call" }
                "missed" -> { receiverCallingStateTextView.text = "Missed call" }
            }
            if (data[position].call?.callSuggestTime != null) {
                receiverCallTimeTextView.text = Utils.dateConverter(data[position].call?.callSuggestTime!!)
            }
        }
    }

}

class ChatRoomDiffUtilCallback(private val oldList: List<ChatRoom>, private val newList: List<ChatRoom>): DiffUtil.Callback() {
    override fun getOldListSize() = oldList.size
    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].createdAt == newList[newItemPosition].createdAt
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}