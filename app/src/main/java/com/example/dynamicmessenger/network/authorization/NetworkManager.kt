package com.example.dynamicmessenger.network.authorization

import com.example.dynamicmessenger.common.MyHeaders
import com.example.dynamicmessenger.common.MyUrls
import com.example.dynamicmessenger.network.authorization.models.*
import com.github.nkzawa.socketio.client.IO
import com.github.nkzawa.socketio.client.Socket
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*
import java.net.URISyntaxException

private val BASE_URL = MyUrls.herokuIP

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .baseUrl(BASE_URL)
    .build()

//Email exist
interface JsonPlaceHolderMailExistApi {
    @Headers(MyHeaders.contentType)
    @POST(MyUrls.mailIsExist)
    fun isMailExist(@Body task: EmailExistTask):
            Call<MailExistProperty>
}

object MailExistApi {
    val retrofitService : JsonPlaceHolderMailExistApi by lazy {
        retrofit.create(
            JsonPlaceHolderMailExistApi::class.java)
    }
}

//Login
interface JsonPlaceHolderLoginApi {
    @Headers(MyHeaders.accept)
    @POST(MyUrls.login)
    fun loginResponse(@Body task: LoginTask):
            Call<LoginProperty>

}

object LoginApi {
    val retrofitService : JsonPlaceHolderLoginApi by lazy {
        retrofit.create(
            JsonPlaceHolderLoginApi::class.java)
    }
}

//Registration
interface JsonPlaceHolderRegistrationApi {
    @Headers(MyHeaders.accept)
    @POST(MyUrls.reg)
    fun registrationResponse(@Body loginTask: LoginTask):
            Call<RegistrationProperty>
}

object RegistrationApi {
    val retrofitService : JsonPlaceHolderRegistrationApi by lazy {
        retrofit.create(
            JsonPlaceHolderRegistrationApi::class.java)
    }
}

//Update User
interface JsonPlaceHolderUpdateUserApi {
    @Headers(MyHeaders.accept)
    @POST(MyUrls.updateUser)
    fun updateUserResponse(@Header (MyHeaders.tokenAuthorization) header: String,
                           @Body updateUserTask: UpdateUserTask):
            Call<UpdateUserProperty>
}

object UpdateUserApi {
    val retrofitService : JsonPlaceHolderUpdateUserApi by lazy {
        retrofit.create(
            JsonPlaceHolderUpdateUserApi::class.java)
    }
}

//User token verify
interface JsonPlaceHolderUserTokenVerifyApi {
    @POST(MyUrls.verifyToken)
    fun userTokenResponse(@Header (MyHeaders.tokenAuthorization) header: String):
            Call<UserTokenProperty>
}

object UserTokenVerifyApi {
    val retrofitService : JsonPlaceHolderUserTokenVerifyApi by lazy {
        retrofit.create(
            JsonPlaceHolderUserTokenVerifyApi::class.java)
    }
}

//University
interface JsonPlaceHolderUniversityApi {
    @Headers(MyHeaders.accept)
    @GET(MyUrls.allUniversity)
    fun universityResponse(@Header (MyHeaders.tokenAuthorization) header: String):
            Call<List<UniversityProperty>>
}

object UniversityApi {
    val retrofitService : JsonPlaceHolderUniversityApi by lazy {
        retrofit.create(
            JsonPlaceHolderUniversityApi::class.java)
    }
}

//User contacts
interface JsonPlaceHolderContactsApi {
    @Headers(MyHeaders.accept)
    @GET(MyUrls.contacts)
    fun contactsResponse(@Header (MyHeaders.tokenAuthorization) header: String):
            Call<List<UserContacts>>
}

object ContactsApi {
    val retrofitService : JsonPlaceHolderContactsApi by lazy {
        retrofit.create(
            JsonPlaceHolderContactsApi::class.java
        )
    }
}

//Search contacts
interface JsonPlaceHolderSearchContactsApi {
    @Headers(MyHeaders.accept)
    @POST(MyUrls.searchContacts)
    fun contactsSearchResponse(@Header (MyHeaders.tokenAuthorization) header: String,
                               @Body term: SearchTask) :
            Call<Users>
}

object SearchContactsApi {
    val retrofitService : JsonPlaceHolderSearchContactsApi by lazy {
        retrofit.create(
            JsonPlaceHolderSearchContactsApi::class.java
        )
    }
}

//Add Contact
interface JsonPlaceHolderAddContactApi {
    @Headers(MyHeaders.accept)
    @POST(MyUrls.addContact)
    fun addContactResponse(@Header (MyHeaders.tokenAuthorization) header: String,
                           @Body userID : AddUserContactTask) :
            Call<Void>
}

object AddContactApi {
    val retrofitService : JsonPlaceHolderAddContactApi by lazy {
        retrofit.create(
            JsonPlaceHolderAddContactApi::class.java
        )
    }
}

//Logout
interface JsonPlaceHolderLogoutApi {
    @Headers(MyHeaders.accept)
    @POST(MyUrls.logout)
    fun logoutResponse(@Header (MyHeaders.tokenAuthorization) header: String) :
            Call<Void>
}

object LogoutApi {
    val retrofitService : JsonPlaceHolderLogoutApi by lazy {
        retrofit.create(
            JsonPlaceHolderLogoutApi::class.java
        )
    }
}

//Chats
interface JsonPlaceHolderChatsApi {
    @Headers(MyHeaders.accept)
    @GET(MyUrls.chats)
    fun chatsResponse(@Header (MyHeaders.tokenAuthorization) header: String) :
            Call<List<Chat>>
}

object ChatsApi {
    val retrofitService : JsonPlaceHolderChatsApi by lazy {
        retrofit.create(
            JsonPlaceHolderChatsApi::class.java
        )
    }
}

//Chat room
interface JsonPlaceHolderChatRoomApi {
    @Headers(MyHeaders.accept)
    @GET("${MyUrls.chats}{id}")
    fun chatRoomResponse(@Header (MyHeaders.tokenAuthorization) header: String,
                         @Path ("id") receiverId: String) :
            Call<List<ChatRoom>>
}

object ChatRoomApi {
    val retrofitService : JsonPlaceHolderChatRoomApi by lazy {
        retrofit.create(
            JsonPlaceHolderChatRoomApi::class.java
        )
    }
}