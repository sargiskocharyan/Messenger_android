package com.example.dynamicmessenger.network.authorization

import android.graphics.Bitmap
import com.example.dynamicmessenger.common.MyHeaders
import com.example.dynamicmessenger.common.ResponseUrls
import com.example.dynamicmessenger.network.authorization.models.*
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*

private val BASE_URL = ResponseUrls.herokuIP
private val ERO_URL = ResponseUrls.ErosServerIP

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .baseUrl(BASE_URL)
    .build()

private val retrofitEro = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .baseUrl(ERO_URL)
    .build()

//Email exist
interface JsonPlaceHolderMailExistApi {
    @Headers(MyHeaders.contentType)
    @POST(ResponseUrls.mailIsExist)
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
    @POST(ResponseUrls.login)
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
    @POST(ResponseUrls.reg)
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
    @POST(ResponseUrls.updateUser)
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
    @POST(ResponseUrls.verifyToken)
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
    @GET(ResponseUrls.allUniversity)
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
    @GET(ResponseUrls.contacts)
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
    @POST(ResponseUrls.searchContacts)
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
    @POST(ResponseUrls.addContact)
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
    @POST(ResponseUrls.logout)
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
    @GET("${ResponseUrls.chats}/")
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
    @GET("${ResponseUrls.chats}/{id}")
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

//Receive Avatar
interface JsonPlaceHolderGetAvatarApi {
    @Headers(MyHeaders.accept)
    @GET("${ResponseUrls.users}/{id}/${ResponseUrls.avatar}")
    fun getAvatarResponse(@Header (MyHeaders.tokenAuthorization) header: String,
                          @Path ("id") receiverId: String) :
            Call<ResponseBody>
}

object GetAvatarApi {
    val retrofitService : JsonPlaceHolderGetAvatarApi by lazy {
        retrofit.create(
            JsonPlaceHolderGetAvatarApi::class.java
        )
    }
}

//Load Avatar
interface JsonPlaceHolderSaveAvatarApi {
    @Multipart
    @Headers(MyHeaders.accept)
    @POST(ResponseUrls.saveAvatar)
    fun saveAvatarResponse(@Header (MyHeaders.tokenAuthorization) header: String,
                           @Part avatar : MultipartBody.Part) :
            Call<Void>
}

object SaveAvatarApi {
    val retrofitService : JsonPlaceHolderSaveAvatarApi by lazy {
        retrofit.create(
            JsonPlaceHolderSaveAvatarApi::class.java
        )
    }
}