package com.example.dynamicmessenger.dialogs

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import com.example.dynamicmessenger.R
import com.example.dynamicmessenger.network.authorization.SearchContactsApi
import com.example.dynamicmessenger.network.authorization.models.SearchTask
import com.example.dynamicmessenger.network.authorization.models.UserContacts
import com.example.dynamicmessenger.network.authorization.models.Users
import com.example.dynamicmessenger.userDataController.SaveToken
import com.example.dynamicmessenger.userDataController.SharedPreferencesManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ContactsSearchDialog(val myClosure: (List<UserContacts>) -> Unit): AppCompatDialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        val inflater: LayoutInflater = requireActivity().layoutInflater
        val view: View = inflater.inflate(R.layout.contacts_search_alert, null, false)
        builder.setView(view)
            .setTitle("Search")
            .setMessage("Enter Contact name, last name or username")
            .setPositiveButton("Search",
                DialogInterface.OnClickListener { _, _ ->
                    val name = view.findViewById<EditText>(R.id.searchUsername).text.toString()
                    val myEncryptedToken = SharedPreferencesManager.getUserToken(requireContext())
                    val myToken = SaveToken.decrypt(myEncryptedToken)
                    val task = SearchTask(name)
                    val getProperties: Call<Users> = SearchContactsApi.retrofitService.contactsSearchResponse(myToken, task)
                    try {
                        getProperties.enqueue(object : Callback<Users?> {
                            override fun onResponse(
                                call: Call<Users?>,
                                response: Response<Users?>
                            ) {
                                if (response.isSuccessful) {
                                    myClosure(response.body()!!.users)
                                } else {
                                    Toast.makeText(context, "Something gone a wrong", Toast.LENGTH_SHORT).show()
                                }
                            }
                            override fun onFailure(
                                call: Call<Users?>,
                                t: Throwable
                            ) {
                                Toast.makeText(context, "Check your internet connection", Toast.LENGTH_SHORT).show()
                            }
                        })
                    } catch (e: Exception) {
                        Toast.makeText(context, "Something gone a wrong", Toast.LENGTH_SHORT).show()
                    }
                })
        return builder.create()
    }
}