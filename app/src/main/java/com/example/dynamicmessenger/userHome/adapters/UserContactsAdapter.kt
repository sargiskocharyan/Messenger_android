package com.example.dynamicmessenger.userHome.adapters

import android.content.Context
import android.util.Log
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
import com.example.dynamicmessenger.network.GetUserInfoByIdApi
import com.example.dynamicmessenger.network.authorization.models.AddUserContactTask
import com.example.dynamicmessenger.network.authorization.models.User
import com.example.dynamicmessenger.userChatRoom.fragments.ChatRoomFragment
import com.example.dynamicmessenger.userChatRoom.fragments.OpponentInformationFragment
import com.example.dynamicmessenger.userDataController.SharedPreferencesManager
import com.example.dynamicmessenger.userHome.viewModels.UserContactsViewModel
import kotlinx.coroutines.launch

class UserContactsAdapter(val context: Context, val viewModel: UserContactsViewModel): RecyclerView.Adapter<UserContactsAdapter.UserContactsViewHolder>() {
      var data = listOf<User>()
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
        var userContact: User? = null
        val username: TextView = itemView.findViewById(R.id.usernameTextView)
        val name: TextView = itemView.findViewById(R.id.userNameTextView)
        val lastname: TextView = itemView.findViewById(R.id.userLastnameTextView)
        val contactsUserImageView: ImageView = itemView.findViewById(R.id.contactsUserImageView)
        init {
            itemView.setOnClickListener {
                viewModel.viewModelScope.launch {
                    try {
                        val response = GetUserInfoByIdApi.retrofitService.getUserInfoByIdResponseAsync(SharedConfigs.token, userContact!!._id)
                        if (response.isSuccessful) {
                            HomeActivity.opponentUser = response.body()
                            HomeActivity.receiverID = userContact!!._id
                            (context as AppCompatActivity?)!!.supportFragmentManager
                                .beginTransaction()
                                .replace(R.id.fragmentContainer , OpponentInformationFragment())
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