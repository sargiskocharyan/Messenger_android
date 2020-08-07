package com.example.dynamicmessenger.userDataController.database

import android.util.Log
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.dynamicmessenger.network.authorization.models.University

@Entity(tableName = "signed_user")
data class SignedUser(
//    @ColumnInfo(name = "name")
    @PrimaryKey
    val _id: String,
    var gender: String?,
    var name: String?,
    var lastname: String?,
    var username: String?,
    var email: String?,
    var birthday: String?,
    var phoneNumber: String?,
    var info: String?,
    var avatarURL: String?
)

@Entity(tableName = "user_token")
data class UserToken(
    @PrimaryKey
    val token: String,
    val tokenExpire: String
)

@Entity(tableName = "user_calls")
data class UserCalls(
    val _id: String,
    val name: String?,
    val lastname: String?,
    val username: String?,
    var avatarURL: String?,
    @PrimaryKey
    var time: Long = System.currentTimeMillis(),
    var callingState: Int,   //1-outgoing video call, 2-incoming video call
    var duration: Long = time
)

//@Entity(tableName = "user_calls")
//data class UserCalls(
//    val type: String,
//    val status: String?,
//    val participants: ArrayList<String>,
//    @PrimaryKey
//    val _id: String,
//    val callSuggestTime: String?,
//    val caller: String?,
//    var message: String?,
//    val createdAt: String?,
//    val updatedAt: String?,
//    val callStartTime: String?,
//    val callEndTime: String?
//)

@Entity(tableName = "contacts")
data class Contacts(
    @PrimaryKey
    val _id: String
)
