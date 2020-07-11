package com.example.dynamicmessenger.utils

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import com.example.dynamicmessenger.R

class MyAlertMessage {
    companion object {
        fun showAlertDialog(context: Context?, message: String) {
            AlertDialog.Builder(context)
                .setTitle(R.string.error_message)
                .setMessage(message)
                .setPositiveButton(R.string.ok,
                    DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })
                .create().show()
        }
    }
}