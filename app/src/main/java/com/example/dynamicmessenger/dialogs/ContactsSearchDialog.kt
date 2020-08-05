package com.example.dynamicmessenger.dialogs

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import com.example.dynamicmessenger.R
import com.example.dynamicmessenger.activitys.HomeActivity
import com.example.dynamicmessenger.common.SharedConfigs
import com.example.dynamicmessenger.network.SearchContactsApi
import com.example.dynamicmessenger.network.authorization.models.SearchTask
import com.example.dynamicmessenger.network.authorization.models.User
import com.example.dynamicmessenger.utils.Validations
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


class ContactsSearchDialog(val searchResult: MutableLiveData<String>): AppCompatDialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        val inflater: LayoutInflater = requireActivity().layoutInflater
        val view: View = inflater.inflate(R.layout.contacts_search_alert, null, false)
        val name = view.findViewById<EditText>(R.id.searchUsername)
        name.addTextChangedListener(object : TextWatcher {
            @SuppressLint("ResourceAsColor")
            override fun afterTextChanged(s: Editable?) {
                searchResult.value = name.text.toString()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        builder.setView(view)
            .setTitle(R.string.search_user)
            .setMessage(R.string.enter_name_or_lastname_or_username)
            .setPositiveButton(R.string.find) { _, _ -> }
        return builder.create()
    }
}