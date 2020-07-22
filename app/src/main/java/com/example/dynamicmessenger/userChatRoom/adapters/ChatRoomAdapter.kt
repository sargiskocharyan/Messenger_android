package com.example.dynamicmessenger.userChatRoom.adapters

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.dynamicmessenger.R
import com.example.dynamicmessenger.network.authorization.models.Chat
import com.example.dynamicmessenger.network.authorization.models.ChatRoom
import com.example.dynamicmessenger.userHome.adapters.UserChatDiffUtilCallback

class ChatRoomAdapter(val context: Context, private val myID: String) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

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
        private val receiverImageView: ImageView = itemView.findViewById(R.id.receiverImageView)
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

}

class ChatRoomDiffUtilCallback(private val oldList: List<ChatRoom>, private val newList: List<ChatRoom>): DiffUtil.Callback() {
    override fun getOldListSize() = oldList.size
    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].text == newList[newItemPosition].text
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}