package com.example.dynamicmessenger.userHome.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.dynamicmessenger.R
import com.example.dynamicmessenger.activitys.HomeActivity
import com.example.dynamicmessenger.common.SharedConfigs
import com.example.dynamicmessenger.network.GetUserInfoByIdApi
import com.example.dynamicmessenger.network.authorization.models.Chat
import com.example.dynamicmessenger.userCalls.CallRoomActivity
import com.example.dynamicmessenger.userChatRoom.fragments.OpponentInformationFragment
import com.example.dynamicmessenger.userDataController.database.DiskCache
import com.example.dynamicmessenger.userDataController.database.SignedUserDatabase
import com.example.dynamicmessenger.userDataController.database.UserCalls
import com.example.dynamicmessenger.userDataController.database.UserCallsRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class UserCallsAdapter(val context: Context) : RecyclerView.Adapter<UserCallsAdapter.UserCallsViewHolder>() {
    private var data = mutableListOf<UserCalls>()

    fun setAdapterData(userCalls: List<UserCalls>){
        data.clear()
        data.addAll(userCalls)
        notifyDataSetChanged()
    }

    fun submitList(newList: List<UserCalls>) {
        val userChatDiffUtilCallback = UserCallsDiffUtilCallback(data, newList)
        val authorDiffResult = DiffUtil.calculateDiff(userChatDiffUtilCallback)
        authorDiffResult.dispatchUpdatesTo(this)
        data.clear()
        data.addAll(newList)
    }

    private val diskLruCache = DiskCache.getInstance(SharedConfigs.myContext)
    private val callsDao = SignedUserDatabase.getUserDatabase(context)!!.userCallsDao()
    private val callsRepository = UserCallsRepository(callsDao)

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: UserCallsViewHolder, position: Int) {
        val item = data[position]
        holder.userCalls = item
        if (item?.name != null) {
            holder.name.text = item.name
            holder.lastname.text = item.lastname
        } else {
            holder.name.text = item?.username
        }

        holder.callTime.text = item?.time?.let { convertLongToTime(it) }
        if (item?.avatarURL != null) {
            if (diskLruCache.get(item.avatarURL!!) != null) {
                holder.userImageView.setImageBitmap(diskLruCache.get(item.avatarURL!!)!!)
            }
        } else {
            holder.userImageView.setImageResource(R.drawable.ic_user_image)
        }

    }
    @SuppressLint("SimpleDateFormat")
    fun convertLongToTime(time: Long): String {
        val date = Date(time)
        val currentDate: Date = Calendar.getInstance().time
        return if ((currentDate.day == date.day) && (currentDate.month == date.month) && (currentDate.year == date.year)) {
            val newFormat = SimpleDateFormat("HH:mm:ss")
            newFormat.format(date)
        } else if ((currentDate.day != date.day) || (currentDate.month != date.month) && (currentDate.year == date.year)) {
            val newFormat = SimpleDateFormat("MMMM-dd")
            newFormat.format(date)
        } else {
            Resources.getSystem().getString(R.string.long_time_ago)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserCallsAdapter.UserCallsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.calls_item_view, parent, false)
        return UserCallsViewHolder(view, context)
    }

    inner class UserCallsViewHolder(itemView: View, context: Context) : RecyclerView.ViewHolder(itemView){
        var userCalls: UserCalls? = null
        val name: TextView = itemView.findViewById(R.id.callUserNameTextView)
        val lastname: TextView = itemView.findViewById(R.id.callUserLastnameTextView)
        val userImageView: ImageView = itemView.findViewById(R.id.callUserImageView)
        val callTime: TextView = itemView.findViewById(R.id.callMessageTimeTextView)
        init {
            itemView.setOnClickListener {
                SharedConfigs.callingOpponentId = userCalls!!._id
                val intent = Intent(context, CallRoomActivity::class.java)
                userCalls!!.time = System.currentTimeMillis()
                callsRepository.insert(userCalls!!)
                context.startActivity(intent)
                (context as Activity?)!!.overridePendingTransition(1, 1)
            }
        }
    }
}

class UserCallsDiffUtilCallback(private val oldList: List<UserCalls>, private val newList: List<UserCalls>): DiffUtil.Callback() {
    override fun getOldListSize() = oldList.size
    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].time == newList[newItemPosition].time
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].time == newList[newItemPosition].time
    }

}