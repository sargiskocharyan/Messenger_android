package com.example.dynamicmessenger.utils

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface

class MyAlertMessage {
    companion object {
        fun showAlertDialog(context: Context?, message: String) {
            AlertDialog.Builder(context)
                .setTitle("Error")
                .setMessage(message)
                .setPositiveButton("ok",
                    DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })
                .create().show()
        }
    }
}