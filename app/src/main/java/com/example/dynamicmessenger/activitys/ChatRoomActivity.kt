package com.example.dynamicmessenger.activitys

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.dynamicmessenger.R

class ChatRoomActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.supportActionBar!!.hide()
        setContentView(R.layout.activity_chat_room)
    }
}