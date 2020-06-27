package com.example.dynamicmessenger.network.authorization.models

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.text.SimpleDateFormat

@Parcelize
data class MailExistProperty(val mailExist: Boolean, val code: String) : Parcelable

@Parcelize
data class LoginProperty(val token: String, val user: User) : Parcelable

@Parcelize
data class RegistrationProperty(val token: String, val user: User) : Parcelable //TODO

@Parcelize
data class UpdateUserProperty(val _id: String, val name: String, val lastname: String, val university: University, val email: String, val username: String) : Parcelable

@Parcelize
data class University(val _id: String, val name: String ) : Parcelable

@Parcelize
data class UserTokenProperty(val tokenExists: Boolean) : Parcelable

@Parcelize
data class User(val name: String? = null, val lastname: String? = null, val _id: String, val email: String, val username: String, val university: University? = null) : Parcelable

@Parcelize
data class UserInfo(val name: String? = null, val lastname: String? = null, val _id: String, val email: String, val username: String) : Parcelable

@Parcelize
data class UserContacts(val  _id: String, val name: String?, val lastname: String?, val username: String) : Parcelable

@Parcelize
data class Users(val users: List<UserContacts>) : Parcelable

@Parcelize
data class Chat(val id: String, val name: String?, val lastname: String?, val username: String, val message: Message?) : Parcelable

@Parcelize
data class Message(val sender: Sender, val text: String, var createdAt: String, val reciever: String) : Parcelable, Comparable<Message> {
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
data class Sender(val id: String, val name: String) : Parcelable

@Parcelize
data class ChatRoom(val sender: Sender, val text: String, val reciever: String) : Parcelable

//@Parcelize
//data class ImageURI(val avatar: Data)

//{
//    "sender": {
//    "id": "5ee76c90bfa0980017a39013",
//    "name": "David"
//},
//    "_id": "5ee77bb63a69520017c21f7c",
//    "reciever": "5ee76d13bfa0980017a39018",
//    "owner": "5ee77bb03a69520017c21f7b",
//    "text": "privet",
//    "createdAt": "2020-06-15T13:46:30.916Z",
//    "updatedAt": "2020-06-15T13:46:30.916Z",
//    "__v": 0
//},
@Parcelize
data class UniversityProperty(val _id: String, val name: String) : Parcelable {
    override fun toString(): String {
        return name
    }
}

@Parcelize
data class UniversityPropertyList(val list: List<UniversityProperty>) : Parcelable {
    override fun toString(): String {
        return list.toString()
    }
}

