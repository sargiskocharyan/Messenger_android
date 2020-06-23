package com.example.dynamicmessenger.userHome.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.dynamicmessenger.R
import com.example.dynamicmessenger.activitys.ChatRoomActivity
import com.example.dynamicmessenger.activitys.HomeActivity
import com.example.dynamicmessenger.common.IntentExtra
import com.example.dynamicmessenger.network.authorization.models.Chat
import com.example.dynamicmessenger.network.authorization.models.UserContacts
import com.example.dynamicmessenger.userChatRoom.fragments.ChatRoomFragment
import com.example.dynamicmessenger.userDataController.SharedPreferencesManager
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
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
            Log.i("+++", item.message?.toString())
            val currentDate: Date = Calendar.getInstance().time
            if ((currentDate.day == date.day) && (currentDate.month == date.month) && (currentDate.year == date.year)) {
                val newFormat = SimpleDateFormat("HH:mm")
                timeInHours = newFormat.format(date)
                holder.messageTime.text = timeInHours
            } else {
                val newFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")
                timeInHours = newFormat.format(date)
                holder.messageTime.text = timeInHours
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        holder.chat = item
        holder.lastname.text = item.lastname
        holder.name.text = item.name
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