package com.example.dynamicmessenger.userHome.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import com.example.dynamicmessenger.R
import com.example.dynamicmessenger.activitys.ChatRoomActivity
import com.example.dynamicmessenger.network.authorization.AddContactApi
import com.example.dynamicmessenger.network.authorization.models.AddUserContactTask
import com.example.dynamicmessenger.network.authorization.models.UserContacts
import com.example.dynamicmessenger.userDataController.SaveToken
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
        init {
            itemView.setOnClickListener {
                val myEncryptedToken = SharedPreferencesManager.getUserToken(context)
                val myToken = SaveToken.decrypt(myEncryptedToken)
                val task = AddUserContactTask(userContact!!._id)
                if (SharedPreferencesManager.getIsAddContacts(context)) {
                    viewModel.viewModelScope.launch {
                        try {
                            val response = AddContactApi.retrofitService.addContactResponseAsync(myToken!!, task).await()
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
                    val intent = Intent(context, ChatRoomActivity::class.java)
//                intent.putExtra(IntentExtra.receiverId, chat!!.id)
                    SharedPreferencesManager.setReceiverID(context, userContact!!._id)
                    context.startActivity(intent)
                    (context as Activity?)!!.overridePendingTransition(1, 1)
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