package com.example.dynamicmessenger.userHome.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.dynamicmessenger.R
import com.example.dynamicmessenger.activitys.HomeActivity
import com.example.dynamicmessenger.common.SharedConfigs
import com.example.dynamicmessenger.network.AddContactApi
import com.example.dynamicmessenger.network.authorization.models.AddUserContactTask
import com.example.dynamicmessenger.network.authorization.models.UserContacts
import com.example.dynamicmessenger.userChatRoom.fragments.ChatRoomFragment
import com.example.dynamicmessenger.userDataController.SharedPreferencesManager
import com.example.dynamicmessenger.userHome.viewModels.UserContactsViewModel
import kotlinx.coroutines.launch

class UserContactsAdapter(val context: Context, val viewModel: UserContactsViewModel): RecyclerView.Adapter<UserContactsAdapter.UserContactsViewHolder>() {
      var data = listOf<UserContacts>()
          set(value) {
              field = value
              notifyDataSetChanged()
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
            viewModel.getAvatar(item.avatarURL) {
                holder.contactsUserImageView.setImageBitmap(it)
            }
        } else  {
            holder.contactsUserImageView.setImageResource(R.drawable.ic_user_image)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserContactsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.contacts_item_view, parent, false)
        return UserContactsViewHolder(view, context)
    }

    inner class UserContactsViewHolder(itemView: View, context: Context) : RecyclerView.ViewHolder(itemView){
        var userContact: UserContacts? = null
        val username: TextView = itemView.findViewById(R.id.usernameTextView)
        val name: TextView = itemView.findViewById(R.id.userNameTextView)
        val lastname: TextView = itemView.findViewById(R.id.userLastnameTextView)
        val contactsUserImageView: ImageView = itemView.findViewById(R.id.contactsUserImageView)
        init {
            itemView.setOnClickListener {
                val task = AddUserContactTask(userContact!!._id)
                if (SharedPreferencesManager.getIsAddContacts(context)) {
                    viewModel.viewModelScope.launch {
                        try {
                            val response = AddContactApi.retrofitService.addContactResponseAsync(
                                SharedConfigs.token, task)
                            if (response.isSuccessful) {
                                updateRecycleView()
                                SharedPreferencesManager.isAddContacts(context, false)
                            } else {
                                Toast.makeText(context, "User is already in your contacts", Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(context, "Check your internet connection", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
//                    SharedPreferencesManager.setReceiverID(context, userContact!!._id)
                    HomeActivity.userContactsInfo = userContact
                    HomeActivity.receiverID = userContact!!._id
                    (context as AppCompatActivity?)!!.supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.fragmentContainer , ChatRoomFragment())
                        .addToBackStack(null)
                        .commit()
                }
            }
        }
    }

    private fun updateRecycleView() {
        viewModel.getUserContactsFromNetwork(context) {
            data = it
            notifyDataSetChanged()
        }
    }

}

class UserContactsDiffUtilCallback(private val oldList: List<UserContacts>, private val newList: List<UserContacts>): DiffUtil.Callback() {
    override fun getOldListSize() = oldList.size
    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition]._id == newList[newItemPosition]._id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}