package com.example.dynamicmessenger.dialogs

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import com.example.dynamicmessenger.R

class DeleteUserDialog(val myClosure: (Boolean) -> Unit): AppCompatDialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(R.string.attention)
            .setMessage(R.string.are_you_sure_want_to_delete_your_account_your_information_will_be_lost)
            .setPositiveButton(
                R.string.delete,
                DialogInterface.OnClickListener { _, _ ->
                    myClosure(true)
                })
            .setNegativeButton(
                R.string.cancel,
                DialogInterface.OnClickListener { _, _ ->
                    myClosure(false)
                }
            )
        return builder.create()
    }
}