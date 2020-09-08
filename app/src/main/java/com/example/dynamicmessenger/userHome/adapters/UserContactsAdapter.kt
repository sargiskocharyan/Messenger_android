package com.example.dynamicmessenger.userHome.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.dynamicmessenger.R
import com.example.dynamicmessenger.activitys.HomeActivity
import com.example.dynamicmessenger.common.SharedConfigs
import com.example.dynamicmessenger.network.authorization.models.User
import com.example.dynamicmessenger.router.Router
import com.example.dynamicmessenger.userCalls.CallRoomActivity
import com.example.dynamicmessenger.userChatRoom.fragments.ChatRoomFragment
import com.example.dynamicmessenger.userChatRoom.fragments.OpponentInformationFragment
import com.example.dynamicmessenger.utils.observeOnceWithoutOwner
import com.example.dynamicmessenger.common.MyFragments as MyFragments

class UserContactsAdapter(val context: Context): RecyclerView.Adapter<UserContactsAdapter.UserContactsViewHolder>() {
    var data = mutableListOf<User>()

    fun setAdapterDataNotify(newList: List<User>) {
        data.clear()
        data.addAll(newList)
        notifyDataSetChanged()
    }

    fun submitList(newList: List<User>) {
        val userChatDiffUtilCallback = UserContactsDiffUtilCallback(data, newList)
        val authorDiffResult = DiffUtil.calculateDiff(userChatDiffUtilCallback)
        authorDiffResult.dispatchUpdatesTo(this)
        data.clear()
        data.addAll(newList)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: UserContactsViewHolder, position: Int) {
        val item = data[position]
        holder.userContact = item
        holder.username.text = item.username
        holder.name.text = item.name
        holder.lastname.text = item.lastname
        if (item.avatarURL != null) {
            SharedConfigs.userRepository.getAvatar(item.avatarURL).observeOnceWithoutOwner(Observer {
                holder.contactsUserImageView.setImageBitmap(it)
            })
        } else  {
            holder.contactsUserImageView.setImageResource(R.drawable.ic_user_image)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserContactsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_view_contacts, parent, false)
        return UserContactsViewHolder(view, context)
    }

    inner class UserContactsViewHolder(itemView: View, context: Context) : RecyclerView.ViewHolder(itemView){
        var userContact: User? = null
        val username: TextView = itemView.findViewById(R.id.usernameTextView)
        val name: TextView = itemView.findViewById(R.id.userNameTextView)
        val lastname: TextView = itemView.findViewById(R.id.userLastnameTextView)
        val contactsUserImageView: ImageView = itemView.findViewById(R.id.contactsUserImageView)
        init {
            itemView.setOnClickListener {
                HomeActivity.receiverID = userContact!!._id
                if (SharedConfigs.lastFragment == MyFragments.CHATS) {
                    SharedConfigs.userRepository.getUserInformation(userContact!!._id).observe((context as AppCompatActivity), Observer {
                        HomeActivity.opponentUser = it
                    })
                    Router.navigateToFragment(context, ChatRoomFragment())
                    return@setOnClickListener
                }

                if (SharedConfigs.lastFragment == MyFragments.CALLS) {
                    SharedConfigs.callingOpponentId = userContact!!._id
                    val intent = Intent(context, CallRoomActivity::class.java)
                    context.startActivity(intent)
                    return@setOnClickListener
                }

                nextPage()
            }
        }

        private fun nextPage() {
            Router.navigateToFragment((context as AppCompatActivity), OpponentInformationFragment())
        }
    }

}

class UserContactsDiffUtilCallback(private val oldList: List<User>, private val newList: List<User>): DiffUtil.Callback() {
    override fun getOldListSize() = oldList.size
    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition]._id == newList[newItemPosition]._id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}