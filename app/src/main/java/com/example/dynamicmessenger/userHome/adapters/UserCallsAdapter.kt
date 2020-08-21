package com.example.dynamicmessenger.userHome.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
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
import com.example.dynamicmessenger.network.authorization.models.User
import com.example.dynamicmessenger.userCalls.CallRoomActivity
import com.example.dynamicmessenger.userCalls.fragments.CallInformationFragment
import com.example.dynamicmessenger.userDataController.database.*
import com.example.dynamicmessenger.userHome.viewModels.UserCallViewModel
import com.example.dynamicmessenger.utils.Utils

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
        SharedConfigs.userRepository.deleteCallById(data[position]._id)
        data.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: UserCallsViewHolder, position: Int) {
        val item = data[position]
        val opponentId: String = if (item.caller == SharedConfigs.signedUser?._id ?: "") {
                item.receiver!!
            } else {
                item.caller!!
            }
        SharedConfigs.userRepository.getUserInformationFromDB(opponentId).let {
            if (it == null) {
                SharedConfigs.userRepository.getUserInformation(opponentId).observeForever { user ->
                    if (user != null) {
                        configureUserInformation(user, holder)
                    }
                }
            } else {
                configureUserInformation(it, holder)
            }
        }

        val callStartTime = Utils.convertStringToDate(item.callStartTime)
        val callEndTime = Utils.convertStringToDate(item.callEndTime)
        if (callStartTime != null && callEndTime != null) {
            val duration = callEndTime.time - callStartTime.time
            holder.callDuration.text = Utils.getCallDurationInSeconds(duration)
        } else {
            holder.callDuration.text = item.status
        }

        holder.callTime.text = item.callSuggestTime?.let { Utils.dateConverter(it) }

        if (item.status == "accepted") {
            if (item.caller != SharedConfigs.signedUser?._id) {
                holder.callState.setImageResource(R.drawable.ic_incoming_call)
            } else {
                holder.callState.setImageResource(R.drawable.ic_outgoing_call)
            }
        } else if (item.status == "cancelled") {
            if (item.caller != SharedConfigs.signedUser?._id) {
                holder.callState.setImageResource(R.drawable.ic_baseline_call_made_24)
            } else {
                holder.callState.setImageResource(R.drawable.ic_baseline_call_received_24)
            }
        } else if (item.status == "missed") {
            if (item.caller != SharedConfigs.signedUser?._id) {
                holder.callState.setImageResource(R.drawable.ic_baseline_call_missed_24)
            } else {
                holder.callState.setImageResource(R.drawable.ic_baseline_call_missed_outgoing_24)
            }
        }

        holder.userCalls = item

        holder.callInformation.setOnClickListener {
            HomeActivity.receiverID = opponentId
            HomeActivity.callId = item._id
            Log.i("+++", "receiverID ${HomeActivity.receiverID}")
            (context as AppCompatActivity?)!!.supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragmentContainer , CallInformationFragment())
                .addToBackStack(null)
                .commit()
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
        val lastName: TextView = itemView.findViewById(R.id.callUserLastnameTextView)
        val userImageView: ImageView = itemView.findViewById(R.id.callUserImageView)
        val callTime: TextView = itemView.findViewById(R.id.callMessageTimeTextView)
        val callDuration: TextView = itemView.findViewById(R.id.callDurationTextView)
        val callInformation: ImageView = itemView.findViewById(R.id.callInformationImageView)
        val callState: ImageView = itemView.findViewById(R.id.callState)
        init {
            itemView.setOnClickListener {
                val opponentId = if (userCalls!!.caller == SharedConfigs.signedUser?._id ?: "") {
                    userCalls!!.receiver
                } else {
                    userCalls!!.caller
                }
                SharedConfigs.callingOpponentId = opponentId
                val intent = Intent(context, CallRoomActivity::class.java)
                context.startActivity(intent)
            }
        }
    }

    private fun configureUserInformation(user: User, holder: UserCallsViewHolder) {
        if (user.name != null) {
            holder.name.text = user.name
            holder.lastName.text = user.lastname
        } else {
            holder.name.text = user.username
        }

        if (user.avatarURL != null) {
            SharedConfigs.userRepository.getAvatar(user.avatarURL).observeForever {
                holder.userImageView.setImageBitmap(it)
            }
        } else {
            holder.userImageView.setImageResource(R.drawable.ic_user_image)
        }
    }
}

class UserCallsDiffUtilCallback(private val oldList: List<UserCalls>, private val newList: List<UserCalls>): DiffUtil.Callback() {
    override fun getOldListSize() = oldList.size
    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition]._id == newList[newItemPosition]._id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].updatedAt == newList[newItemPosition].updatedAt
    }

}