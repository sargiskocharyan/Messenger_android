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
import com.example.dynamicmessenger.userCalls.CallRoomActivity
import com.example.dynamicmessenger.userCalls.fragments.CallInformationFragment
import com.example.dynamicmessenger.userDataController.database.*
import com.example.dynamicmessenger.userHome.viewModels.UserCallViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class UserCallsAdapter(val context: Context, val viewModel: UserCallViewModel) : RecyclerView.Adapter<UserCallsAdapter.UserCallsViewHolder>() {
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

    fun deleteItem(position: Int) {
        viewModel.deleteCallByTime(data[position].time)
        data.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: UserCallsViewHolder, position: Int) {
        val item = data[position]
        holder.userCalls = item
        if (item.name != null) {
            holder.name.text = item.name
            holder.lastname.text = item.lastname
        } else {
            holder.name.text = item.username
        }

        holder.callTime.text = convertLongToTime(item.time)
        if (item.avatarURL != null) {
            viewModel.getAvatar(item.avatarURL!!) {
                holder.userImageView.setImageBitmap(it)
            }
        } else {
            holder.userImageView.setImageResource(R.drawable.ic_user_image)
        }

        val hours = item.duration / (1000 * 60 * 60) % 24
        val minutes = item.duration / (1000 * 60) % 60
        val seconds = (item.duration / 1000) % 60

        if (minutes == 0L && hours == 0L) {
            holder.callDuration.text = "${seconds}s"
        } else if (hours == 0L) {
            holder.callDuration.text = "${minutes}m ${seconds}s"
        } else {
            holder.callDuration.text = "${hours}h ${minutes}m ${seconds}s"
        }

        when (item.callingState) {
            1 -> holder.callState.setImageResource(R.drawable.ic_outgoing_call)
            2 -> holder.callState.setImageResource(R.drawable.ic_incoming_call)
        }

        holder.callInformation.setOnClickListener {
            if (viewModel.getUserById(data[position]._id) != null) {
                Log.i("+++", "userContacts if")
                HomeActivity.opponentUser = viewModel.getUserById(data[position]._id)
                HomeActivity.receiverID = data[position]._id
                HomeActivity.callTime = data[position].time
                (context as AppCompatActivity?)!!.supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fragmentContainer , CallInformationFragment())
                    .addToBackStack(null)
                    .commit()
            } else {
                viewModel.viewModelScope.launch {
                    try {
                        val response = GetUserInfoByIdApi.retrofitService.getUserInfoByIdResponseAsync(SharedConfigs.token, data[position]._id)
                        if (response.isSuccessful) {
                            response.body()?.let { user -> viewModel.saveUser(user) }
                            Log.i("+++", "userContacts else ${response.body()}")
                            HomeActivity.opponentUser = response.body()
                            HomeActivity.receiverID = data[position]._id
                            HomeActivity.callTime = data[position].time
                            (context as AppCompatActivity?)!!.supportFragmentManager
                                .beginTransaction()
                                .replace(R.id.fragmentContainer , CallInformationFragment())
                                .addToBackStack(null)
                                .commit()
                        } else {
                            Log.i("+++else", "getOpponentInfoFromNetwork $response")
                        }
                    } catch (e: Exception) {
                        Log.i("+++exception", "getOpponentInfoFromNetwork $e")
                    }
                }
            }
        }

    }
    @SuppressLint("SimpleDateFormat")
    fun convertLongToTime(time: Long): String {
        val date = Date(time)
        val currentDate: Date = Calendar.getInstance().time
        return if ((currentDate.day == date.day) && (currentDate.month == date.month) && (currentDate.year == date.year)) {
            val newFormat = SimpleDateFormat("HH:mm")
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
            .inflate(R.layout.item_view_calls, parent, false)
        return UserCallsViewHolder(view, context)
    }

    inner class UserCallsViewHolder(itemView: View, context: Context) : RecyclerView.ViewHolder(itemView){
        var userCalls: UserCalls? = null
        val name: TextView = itemView.findViewById(R.id.callUserNameTextView)
        val lastname: TextView = itemView.findViewById(R.id.callUserLastnameTextView)
        val userImageView: ImageView = itemView.findViewById(R.id.callUserImageView)
        val callTime: TextView = itemView.findViewById(R.id.callMessageTimeTextView)
        val callDuration: TextView = itemView.findViewById(R.id.callDurationTextView)
        val callInformation: ImageView = itemView.findViewById(R.id.callInformationImageView)
        val callState: ImageView = itemView.findViewById(R.id.callState)
        init {
            itemView.setOnClickListener {
                SharedConfigs.callingOpponentId = userCalls!!._id
                val intent = Intent(context, CallRoomActivity::class.java)
                userCalls!!.time = System.currentTimeMillis()
                userCalls!!.callingState = 1
                viewModel.saveCall(userCalls!!)
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