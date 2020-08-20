package com.example.dynamicmessenger.userHome.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.dynamicmessenger.R
import com.example.dynamicmessenger.activitys.HomeActivity
import com.example.dynamicmessenger.common.SharedConfigs
import com.example.dynamicmessenger.network.LoadAvatarApi
import com.example.dynamicmessenger.network.authorization.models.Chat
import com.example.dynamicmessenger.userChatRoom.fragments.ChatRoomFragment
import com.example.dynamicmessenger.userDataController.database.DiskCache
import com.example.dynamicmessenger.utils.Utils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


class UserChatsAdapter(val context: Context) : RecyclerView.Adapter<UserChatsAdapter.UserChatsViewHolder>(){
    var data = mutableListOf<Chat>()

    fun setAdapterDataNotify(newList: List<Chat>) {
        data.clear()
        data.addAll(newList)
        notifyDataSetChanged()
    }

    fun submitList(newList: List<Chat>) {
        val userChatDiffUtilCallback = UserChatDiffUtilCallback(data, newList)
        val authorDiffResult = DiffUtil.calculateDiff(userChatDiffUtilCallback)
        authorDiffResult.dispatchUpdatesTo(this)
        data.clear()
        data.addAll(newList)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserChatsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_view_chats, parent, false)
        return UserChatsViewHolder(view, context)
    }


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: UserChatsViewHolder, position: Int) {
        val item = data[position]
        val chatTime = item.message?.createdAt ?: item.chatCreateDay
        holder.messageTime.text = Utils.dateConverter(chatTime)
        holder.name.text = item.name ?: item.username
        holder.chat = item
        holder.lastname.text = item.lastname
        if (item.message?.type == "call") {//TODO change for all types
            holder.lastMessage.text = "${item.message.call?.type} call ${item.message.call?.duration ?: ""}"
        } else {
            holder.lastMessage.text = item.message?.text
        }
        if (item.recipientAvatarURL != null) {
            SharedConfigs.userRepository.getAvatar(item.recipientAvatarURL).observeForever {
                if (it != null) {
                    holder.chatUserImageView.setImageBitmap(it)
                } else {
                    holder.chatUserImageView.setImageResource(R.drawable.ic_user_image)
                }
            }
        } else  {
            holder.chatUserImageView.setImageResource(R.drawable.ic_user_image)
        }
        SharedConfigs.onlineUsers?.let {onlineUsers ->
            onlineUsers.observeForever {
                if (it.contains(holder.chat!!.id)) {
                    holder.chatUserOnlineStatus.visibility = View.VISIBLE
                } else {
                    holder.chatUserOnlineStatus.visibility = View.INVISIBLE
                }
            }
        }
    }

    @SuppressLint("ResourceType")
    inner class UserChatsViewHolder(itemView: View, context: Context) : RecyclerView.ViewHolder(itemView){
        var chat: Chat? = null
        val name: TextView = itemView.findViewById(R.id.chatsUserName)
        val lastname: TextView = itemView.findViewById(R.id.chatsUserLastname)
        val lastMessage: TextView = itemView.findViewById(R.id.chatsLastMessage)
        val messageTime: TextView = itemView.findViewById(R.id.messageTime)
        val chatUserImageView: ImageView = itemView.findViewById(R.id.chatUserImageView)
        val chatUserOnlineStatus: ImageView = itemView.findViewById(R.id.chatUserOnlineStatus)
        init {
            itemView.setOnClickListener {
                HomeActivity.receiverID = chat!!.id
                (context as AppCompatActivity?)!!.supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fragmentContainer , ChatRoomFragment())
                    .addToBackStack(null)
                    .commit()
            }
        }
    }
}

class UserChatDiffUtilCallback(private val oldList: List<Chat>, private val newList: List<Chat>): DiffUtil.Callback() {
    override fun getOldListSize() = oldList.size
    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].message?.createdAt == newList[newItemPosition].message?.createdAt
    }

}
