package com.example.dynamicmessenger.network.authorization.models

import android.annotation.SuppressLint
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.dynamicmessenger.common.AppLangKeys
import com.example.dynamicmessenger.common.SharedConfigs
import kotlinx.android.parcel.Parcelize
import java.text.SimpleDateFormat

@Parcelize
data class MailExistProperty(val mailExist: Boolean, val code: String) : Parcelable

@Parcelize
data class LoginProperty(val token: String, val user: User) : Parcelable

//@Parcelize
//data class RegistrationProperty(val token: String, val user: User) : Parcelable

@Parcelize
data class UpdateUserProperty(val _id: String, val name: String, val lastname: String, val university: University, val email: String, val username: String, val avatarURL: String?) : Parcelable

@Parcelize
data class University(@PrimaryKey
                      @ColumnInfo(name = "universityId") val _id: String,
                      @ColumnInfo(name = "universityName") val name: String,
                      @ColumnInfo(name = "universityNameRU") val nameRU: String,
                      @ColumnInfo(name = "universityNameEN") val nameEN: String) : Parcelable {
    override fun toString(): String {
        return when (SharedConfigs.appLang) {
            AppLangKeys.AM ->  name
            AppLangKeys.RU ->  nameRU
            else -> nameEN
        }
    }
}

@Parcelize
data class UserTokenProperty(val tokenExists: Boolean) : Parcelable

@Parcelize
data class User(val name: String? = null, val lastname: String? = null, val _id: String, val email: String, val username: String, val university: University? = null, var avatarURL: String?) : Parcelable

@Parcelize
data class UserInfo(val name: String? = null, val lastname: String? = null, val _id: String, val email: String, val username: String) : Parcelable

@Parcelize
data class UserContacts(val  _id: String, val name: String?, val lastname: String?, val username: String, val avatarURL: String?) : Parcelable

@Parcelize
data class Users(val users: List<UserContacts>) : Parcelable

@Parcelize
@Entity(tableName = "user_chat")
data class Chat(
    @PrimaryKey
    val id: String,
    val name: String?,
    val lastname: String?,
    val username: String,
    @Embedded
    val message: Message?,
    val recipientAvatarURL: String?) : Parcelable

@Parcelize
data class Message(
                @Embedded
                val sender: Sender,
                val text: String,
                var createdAt: String,
                val reciever: String) : Parcelable, Comparable<Message> {
    @SuppressLint("SimpleDateFormat")
    override fun compareTo(other: Message): Int {
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        val area1 = format.parse(createdAt)
        val area2 = format.parse(other.createdAt)
        if(area1 == area2){
            return 0;
        }else if(area1 < area2){
            return -1;
        }
        return 1;
    }

}

@Parcelize
data class Sender(
                @PrimaryKey
                @ColumnInfo(name = "senderId") val id: String,
                @ColumnInfo(name = "senderName")val name: String) : Parcelable

@Parcelize
data class ChatRoom(val sender: Sender, val text: String, val reciever: String) : Parcelable

@Parcelize
data class UniversityProperty(val _id: String, val name: String, val nameRU: String , val nameEN: String) : Parcelable {
    override fun toString(): String {
        return when (SharedConfigs.appLang) {
            AppLangKeys.AM ->  name
            AppLangKeys.RU ->  nameRU
            else -> nameEN
        }
    }
}

@Parcelize
data class UniversityPropertyList(val list: List<UniversityProperty>) : Parcelable {
    override fun toString(): String {
        return list.toString()
    }
}


