package com.example.dynamicmessenger.userHome.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.dynamicmessenger.R
import com.example.dynamicmessenger.activitys.ChatRoomActivity
import com.example.dynamicmessenger.network.authorization.models.Chat
import com.example.dynamicmessenger.userDataController.SharedPreferencesManager
import java.text.SimpleDateFormat
import java.util.*


class UserChatsAdapter(val context: Context) : RecyclerView.Adapter<UserChatsAdapter.UserChatsViewHolder>(){
    var data = listOf<Chat>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserChatsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.chats_item_view, parent, false)
        return UserChatsViewHolder(view, context)
    }


    @SuppressLint("SimpleDateFormat")
    override fun onBindViewHolder(holder: UserChatsViewHolder, position: Int) {
        val item = data[position]
        val timeInHours: String
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        format.timeZone = TimeZone.getTimeZone("UTC")
        try {
            val date: Date = format.parse(item.message?.createdAt)!!
            val currentDate: Date = Calendar.getInstance().time
            if ((currentDate.day == date.day) && (currentDate.month == date.month) && (currentDate.year == date.year)) {
                val newFormat = SimpleDateFormat("HH:mm")
                timeInHours = newFormat.format(date)
                holder.messageTime.text = timeInHours
            } else if ((currentDate.day != date.day) || (currentDate.month != date.month) && (currentDate.year == date.year)) {
                val newFormat = SimpleDateFormat("MMMM-dd")
                timeInHours = newFormat.format(date)
                holder.messageTime.text = timeInHours
            } else {
                holder.messageTime.setText(R.string.long_time_ago)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        holder.name.text = item.name ?: item.username
        holder.chat = item
        holder.lastname.text = item.lastname
        holder.lastMessage.text = item.message?.text
    }

    inner class UserChatsViewHolder(itemView: View, context: Context) : RecyclerView.ViewHolder(itemView){
        var chat: Chat? = null
        val name: TextView = itemView.findViewById(R.id.chatsUserName)
        val lastname: TextView = itemView.findViewById(R.id.chatsUserLastname)
        val lastMessage: TextView = itemView.findViewById(R.id.chatsLastMessage)
        val messageTime: TextView = itemView.findViewById(R.id.messageTime)
        init {
            itemView.setOnClickListener {
                val intent = Intent(context, ChatRoomActivity::class.java)
//                intent.putExtra(IntentExtra.receiverId, chat!!.id)
                SharedPreferencesManager.setReceiverID(context, chat!!.id)
                context.startActivity(intent)
                (context as Activity?)!!.overridePendingTransition(1, 1)
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
        return oldList[oldItemPosition] == newList[newItemPosition]
    }

}
