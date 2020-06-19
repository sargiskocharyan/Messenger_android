package com.example.dynamicmessenger.userHome.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.dynamicmessenger.R
import com.example.dynamicmessenger.network.authorization.AddContactApi
import com.example.dynamicmessenger.network.authorization.models.AddUserContactTask
import com.example.dynamicmessenger.network.authorization.models.UserContacts
import com.example.dynamicmessenger.userDataController.SaveToken
import com.example.dynamicmessenger.userDataController.SharedPreferencesManager
import com.example.dynamicmessenger.userHome.viewModels.UserContactsViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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
                val getProperties: Call<Void> = AddContactApi.retrofitService.addContactResponse(myToken, task)
                try {
                    getProperties.enqueue(object : Callback<Void> {
                        override fun onResponse(
                            call: Call<Void>,
                            response: Response<Void>
                        ) {
                            if (response.isSuccessful) {
                                updateRecycleView()
                            } else {
                                Toast.makeText(context, "User is already in your contacts", Toast.LENGTH_SHORT).show()
                            }
                        }
                        override fun onFailure(
                            call: Call<Void>,
                            t: Throwable
                        ) {
                            Toast.makeText(context, "Check your internet connection", Toast.LENGTH_SHORT).show()
                        }
                    })
                } catch (e: Exception) {
                    Toast.makeText(context, "Something gone a wrong", Toast.LENGTH_SHORT).show()
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