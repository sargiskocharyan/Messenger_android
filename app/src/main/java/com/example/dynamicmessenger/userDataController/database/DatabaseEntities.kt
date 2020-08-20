package com.example.dynamicmessenger.userDataController.database

import android.os.Parcelable
import android.util.Log
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

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
    var avatarURL: String?,
    var deviceRegistered: Boolean? = false
)

@Entity(tableName = "user_token")
data class UserToken(
    @PrimaryKey
    val token: String,
    val tokenExpire: String
)

//@Entity(tableName = "user_calls")
//data class UserCalls(
//    val _id: String,
//    val name: String?,
//    val lastname: String?,
//    val username: String?,
//    var avatarURL: String?,
//    @PrimaryKey
//    var time: Long = System.currentTimeMillis(),
//    var callingState: Int,   //1-outgoing video call, 2-incoming video call
//    var duration: Long = time
//)
@Parcelize
@Entity(tableName = "user_calls")
data class UserCalls(
    val type: String,
    val status: String?,
    val participants: List<String>,
    @PrimaryKey
    val _id: String,
    val callSuggestTime: String?,
    val caller: String?,
    val receiver: String?,
    var message: String?,
    val createdAt: String?,
    val updatedAt: String?,
    val callStartTime: String?,
    val callEndTime: String?
) : Parcelable

@Entity(tableName = "contacts")
data class Contacts(
    @PrimaryKey
    val _id: String
)
