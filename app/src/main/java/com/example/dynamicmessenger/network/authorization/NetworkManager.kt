package com.example.dynamicmessenger.network.authorization

import com.example.dynamicmessenger.common.MyHeaders
import com.example.dynamicmessenger.common.ResponseUrls
import com.example.dynamicmessenger.network.authorization.models.*
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.MultipartBody
import retrofit2.Converter
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*
import java.lang.reflect.Type


private const val BASE_URL = ResponseUrls.herokuIP
private const val ERO_URL = ResponseUrls.ErosServerIP
//private const val ERO_URL = ""

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val gson = GsonBuilder()
    .setLenient()
    .create()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi).asLenient())
//    .addConverterFactory(LENIENT_FACTORY.create(moshi))
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .baseUrl(BASE_URL)
    .build()

//val LENIENT_FACTORY: JsonAdapter.Factory = object : JsonAdapter.Factory {
//    override fun create(
//        type: Type?,
//        annotations: Set<Annotation?>?,
//        moshi: Moshi
//    ): JsonAdapter<*>? {
//        return moshi.nextAdapter<Any>(this, type, annotations).lenient()
//    }
//}
private val retrofitEro = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .baseUrl(ERO_URL)
    .build()

//Email exist
interface JsonPlaceHolderMailExistApi {
    @Headers(MyHeaders.contentType)
    @POST(ResponseUrls.mailIsExist)
    suspend fun isMailExistAsync(@Body task: EmailExistTask):
            Response<MailExistProperty>
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
    suspend fun loginResponseAsync(@Body task: LoginTask):
            Response<LoginProperty>

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
    suspend fun registrationResponseAsync(@Body loginTask: LoginTask):
            Response<RegistrationProperty>
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
    suspend fun updateUserResponseAsync(@Header (MyHeaders.tokenAuthorization) header: String,
                                @Body updateUserTask: UpdateUserTask):
            Response<UpdateUserProperty>
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
    suspend fun userTokenResponseAsync(@Header (MyHeaders.tokenAuthorization) header: String):
            Response<UserTokenProperty>
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
    suspend fun universityResponseAsync(@Header (MyHeaders.tokenAuthorization) header: String):
            Response<List<UniversityProperty>>
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
    suspend fun contactsResponseAsync(@Header (MyHeaders.tokenAuthorization) header: String):
            Response<List<UserContacts>>
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
    suspend fun contactsSearchResponseAsync(@Header (MyHeaders.tokenAuthorization) header: String,
                                            @Body term: SearchTask) :
            Response<Users>
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
    suspend fun addContactResponseAsync(@Header (MyHeaders.tokenAuthorization) header: String,
                                        @Body userID : AddUserContactTask) :
            Response<Void>
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
    suspend fun logoutResponseAsync(@Header (MyHeaders.tokenAuthorization) header: String) :
            Response<Void>
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
    suspend fun chatsResponseAsync(@Header (MyHeaders.tokenAuthorization) header: String) :
            Response<List<Chat>>
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
    suspend fun chatRoomResponseAsync(@Header (MyHeaders.tokenAuthorization) header: String,
                                      @Path ("id") receiverId: String) :
            Response<List<ChatRoom>>
}

object ChatRoomApi {
    val retrofitService : JsonPlaceHolderChatRoomApi by lazy {
        retrofit.create(
            JsonPlaceHolderChatRoomApi::class.java
        )
    }
}

//Load Avatar
interface JsonPlaceHolderSaveAvatarApi {
    @Multipart
    @Headers(MyHeaders.accept)
    @POST(ResponseUrls.saveAvatar)
    suspend fun saveAvatarResponseAsync(@Header (MyHeaders.tokenAuthorization) header: String,
                                        @Part avatar : MultipartBody.Part) :
            Response<String>
}

object SaveAvatarApi {
    val retrofitService : JsonPlaceHolderSaveAvatarApi by lazy {
        retrofit.create(
            JsonPlaceHolderSaveAvatarApi::class.java
        )
    }
}