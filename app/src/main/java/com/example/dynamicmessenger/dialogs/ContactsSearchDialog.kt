package com.example.dynamicmessenger.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import com.example.dynamicmessenger.R
import com.example.dynamicmessenger.activitys.HomeActivity
import com.example.dynamicmessenger.common.SharedConfigs
import com.example.dynamicmessenger.network.SearchContactsApi
import com.example.dynamicmessenger.network.authorization.models.SearchTask
import com.example.dynamicmessenger.network.authorization.models.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


class ContactsSearchDialog(private val coroutineScope: CoroutineScope, val myClosure: (List<User>) -> Unit): AppCompatDialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        val inflater: LayoutInflater = requireActivity().layoutInflater
        val view: View = inflater.inflate(R.layout.contacts_search_alert, null, false)
        builder.setView(view)
            .setTitle(R.string.search_user)
            .setMessage(R.string.enter_name_or_lastname_or_username)
            .setPositiveButton(R.string.find) { _, _ ->
                val name = view.findViewById<EditText>(R.id.searchUsername).text.toString()
                val task = SearchTask(name)
                coroutineScope.launch {
                    try {
                        val response = SearchContactsApi.retrofitService.contactsSearchResponseAsync(SharedConfigs.token, task)
                        if (response.isSuccessful) {
                            HomeActivity.isAddContacts = true
                            myClosure(response.body()!!.users)
                        } else {
                            Toast.makeText(context, "Something gone a wrong", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
        //                            Toast.makeText(context, "Check your internet connection", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        return builder.create()
    }
}