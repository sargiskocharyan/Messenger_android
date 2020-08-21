package com.example.dynamicmessenger.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import com.example.dynamicmessenger.R

class DeactivateUserDialog(val myClosure: (Boolean) -> Unit): AppCompatDialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(R.string.attention)
            .setMessage("You are sure?")
            .setPositiveButton(R.string.ok) { _, _ ->
                myClosure(true)
            }
            .setNegativeButton(R.string.cancel) { _, _ ->
                myClosure(false)
            }
        return builder.create()
    }
}