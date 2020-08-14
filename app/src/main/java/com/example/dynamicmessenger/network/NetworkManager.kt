package com.example.dynamicmessenger.network

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import com.example.dynamicmessenger.common.MyHeaders
import com.example.dynamicmessenger.common.ResponseUrls
import com.example.dynamicmessenger.common.SharedConfigs.myContext
import com.example.dynamicmessenger.network.authorization.models.*
import com.example.dynamicmessenger.userDataController.database.SignedUser
import com.example.dynamicmessenger.userDataController.database.UserCalls
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Cache
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import javax.security.cert.CertificateException


//private const val BASE_URL = ResponseUrls.herokuIP
private const val BASE_URL = ResponseUrls.ErosServerIP
private const val ERO_URL = ResponseUrls.ErosServerIP

private var cacheSize: Long = 10 * 1024 * 1024 // 10 MB

private var cache = Cache(myContext.codeCacheDir, cacheSize)

fun networkAvailable(context: Context): Boolean? {
    var isConnected: Boolean? = false // Initial Value
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
    if (activeNetwork != null && activeNetwork.isConnected)
        isConnected = true
    return isConnected
}

fun getUnsafeOkHttpClient(): OkHttpClient? {
    return try {
        // Create a trust manager that does not validate certificate chains
        val trustAllCerts =
            arrayOf<TrustManager>(
                object : X509TrustManager {
                    @SuppressLint("TrustAllX509TrustManager")
                    @Throws(CertificateException::class)
                    override fun checkClientTrusted(
                        chain: Array<X509Certificate>,
                        authType: String
                    ) {
                    }

                    @SuppressLint("TrustAllX509TrustManager")
                    @Throws(CertificateException::class)
                    override fun checkServerTrusted(
                        chain: Array<X509Certificate>,
                        authType: String
                    ) {
                    }

                    override fun getAcceptedIssuers(): Array<X509Certificate?>? {
                        return arrayOfNulls(0)
                    }
                }
            )

        // Install the all-trusting trust manager
        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, trustAllCerts, SecureRandom())
        // Create an ssl socket factory with our all-trusting manager
        val sslSocketFactory: SSLSocketFactory = sslContext.socketFactory
        OkHttpClient.Builder()
            .sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
            .cache(cache)
            .addInterceptor { chain ->
                var request = chain.request()
                request = if (networkAvailable(myContext)!!)
                    request.newBuilder().header("Cache-Control", "public, max-age=" + 10).build()
                else
                    request.newBuilder().header("Cache-Control", "public, only-if-cached, max-stale=" + 60 * 60 * 24 * 7).build()
                chain.proceed(request)
            }
            .hostnameVerifier { _, _ -> true }.build()
    } catch (e: Exception) {
        throw RuntimeException(e)
    }
}

val okHttpClient: OkHttpClient = OkHttpClient.Builder()
    .cache(cache)
    .addInterceptor { chain ->
        var request = chain.request()
        request = if (networkAvailable(myContext)!!)
            request.newBuilder().header("Cache-Control", "public, max-age=" + 10).build()
        else
            request.newBuilder().header("Cache-Control", "public, only-if-cached, max-stale=" + 60 * 60 * 24 * 7).build()
        chain.proceed(request)
    }
    .build()

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi).asLenient())
//    .addConverterFactory(LENIENT_FACTORY.create(moshi = moshi))
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .baseUrl(BASE_URL)
    .client(getUnsafeOkHttpClient()!!)
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
            Response<LoginProperty>
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
            Response<User>
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

//User contacts
interface JsonPlaceHolderContactsApi {
    @Headers(MyHeaders.accept)
    @GET(ResponseUrls.contacts)
    suspend fun contactsResponseAsync(@Header (MyHeaders.tokenAuthorization) header: String):
            Response<List<User>>
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
    //TODO: addContact
    suspend fun addContactResponseAsync(@Header (MyHeaders.tokenAuthorization) header: String,
                                        @Body userID : AddUserContactTask) : Response<Void>
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
    suspend fun logoutUser(@Header (MyHeaders.tokenAuthorization) header: String,
                           @Body deviceUUID : LogoutUserTask) :
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
            Response<ResponseBody>
}

object SaveAvatarApi {
    val retrofitService : JsonPlaceHolderSaveAvatarApi by lazy {
        retrofit.create(
            JsonPlaceHolderSaveAvatarApi::class.java
        )
    }
}

//Delete user
interface JsonPlaceHolderDeleteUserApi {
    @Headers(MyHeaders.accept)
    @DELETE(ResponseUrls.deleteUser)
    suspend fun deleteUserResponseAsync(@Header (MyHeaders.tokenAuthorization) header: String) :
            Response<Void>
}

object DeleteUserApi {
    val retrofitService : JsonPlaceHolderDeleteUserApi by lazy {
        retrofit.create(
            JsonPlaceHolderDeleteUserApi::class.java
        )
    }
}

//Deactivate user
interface JsonPlaceHolderDeactivateUserApi {
    @Headers(MyHeaders.accept)
    @POST(ResponseUrls.deactivateUser)
    suspend fun deactivateUserResponseAsync(@Header (MyHeaders.tokenAuthorization) header: String) :
            Response<Void>
}

object DeactivateUserApi {
    val retrofitService : JsonPlaceHolderDeactivateUserApi by lazy {
        retrofit.create(
            JsonPlaceHolderDeactivateUserApi::class.java
        )
    }
}

//Delete user avatar
interface JsonPlaceHolderDeleteAvatarApi {
    @Headers(MyHeaders.accept)
    @DELETE(ResponseUrls.saveAvatar)
    suspend fun deleteAvatarResponseAsync(@Header (MyHeaders.tokenAuthorization) header: String) :
            Response<Void>
}

object DeleteAvatarApi {
    val retrofitService : JsonPlaceHolderDeleteAvatarApi by lazy {
        retrofit.create(
            JsonPlaceHolderDeleteAvatarApi::class.java
        )
    }
}

//User info by id
interface JsonPlaceHolderGetUserInfoByIdApi {
    @Headers(MyHeaders.accept)
    @GET("${ResponseUrls.userInfoById}/{id}")
    suspend fun getUserInfoByIdResponseAsync(@Header (MyHeaders.tokenAuthorization) header: String,
                                             @Path ("id") receiverId: String) :
            Response<User>
}

object GetUserInfoByIdApi {
    val retrofitService : JsonPlaceHolderGetUserInfoByIdApi by lazy {
        retrofit.create(
            JsonPlaceHolderGetUserInfoByIdApi::class.java
        )
    }
}

//Load Avatar
interface JsonPlaceHolderLoadAvatarApi {
    @GET
    @Streaming
    suspend fun loadAvatarResponseAsync(@Url avatarUrl: String) :
            Response<ResponseBody>
}

object LoadAvatarApi {
    val retrofitService : JsonPlaceHolderLoadAvatarApi by lazy {
        retrofit.create(
            JsonPlaceHolderLoadAvatarApi::class.java
        )
    }
}

//Online users
interface JsonPlaceHolderGetOnlineUsersApi {
    @Headers(MyHeaders.accept)
    @POST(ResponseUrls.onlineUsers)
    suspend fun getOnlineUsers(@Header (MyHeaders.tokenAuthorization) header: String,
                               @Body userArray : OnlineUsersTask) :
            Response<OnlineUsers>
}

object GetOnlineUsersApi {
    val retrofitService : JsonPlaceHolderGetOnlineUsersApi by lazy {
        retrofit.create(
            JsonPlaceHolderGetOnlineUsersApi::class.java
        )
    }
}

//Remove contact
interface JsonPlaceHolderRemoveContactApi {
    @Headers(MyHeaders.accept)
    @POST(ResponseUrls.removeContact)
    suspend fun removeContact(@Header (MyHeaders.tokenAuthorization) header: String,
                               @Body userId : RemoveContactTask) :
            Response<Void>
}

object RemoveContactApi {
    val retrofitService : JsonPlaceHolderRemoveContactApi by lazy {
        retrofit.create(
            JsonPlaceHolderRemoveContactApi::class.java
        )
    }
}

//Hide data
interface JsonPlaceHolderHideDataApi {
    @Headers(MyHeaders.accept)
    @POST(ResponseUrls.hideData)
    suspend fun hideData(@Header (MyHeaders.tokenAuthorization) header: String,
                         @Body userId : HideDataTask) :
            Response<Void>
}

object HideDataApi {
    val retrofitService : JsonPlaceHolderHideDataApi by lazy {
        retrofit.create(
            JsonPlaceHolderHideDataApi::class.java
        )
    }
}

//Username Exists
interface JsonPlaceHolderCheckUsernameExistsApi {
    @Headers(MyHeaders.accept)
    @POST(ResponseUrls.usernameExists)
    suspend fun checkUsernameExists(@Header (MyHeaders.tokenAuthorization) header: String,
                                    @Body userId : UsernameExistsTask) :
            Response<UsernameExists>
}

object CheckUsernameExistsApi {
    val retrofitService : JsonPlaceHolderCheckUsernameExistsApi by lazy {
        retrofit.create(
            JsonPlaceHolderCheckUsernameExistsApi::class.java
        )
    }
}

//Get user calls
interface JsonPlaceHolderGetUserCallsApi {
    @Headers(MyHeaders.accept)
    @GET(ResponseUrls.callHistory)
    suspend fun getUserCalls(@Header (MyHeaders.tokenAuthorization) header: String) :
            Response<List<UserCalls>>
}

object GetUserCallsApi {
    val retrofitService : JsonPlaceHolderGetUserCallsApi by lazy {
        retrofit.create(
            JsonPlaceHolderGetUserCallsApi::class.java
        )
    }
}

//Delete call
interface JsonPlaceHolderDeleteUserCallApi {
    @Headers(MyHeaders.accept)
    @HTTP(method = "DELETE", path = ResponseUrls.call, hasBody = true)
    suspend fun deleteUserCall(@Header (MyHeaders.tokenAuthorization) header: String,
                               @Body callId : DeleteUserCallTask) :
            Response<Void>
}

object DeleteUserCallApi {
    val retrofitService : JsonPlaceHolderDeleteUserCallApi by lazy {
        retrofit.create(
            JsonPlaceHolderDeleteUserCallApi::class.java
        )
    }
}

//Register device
interface JsonPlaceHolderRegisterDeviceApi {
    @Headers(MyHeaders.accept)
    @POST(ResponseUrls.registerDevice)
    suspend fun registerDevice(@Header (MyHeaders.tokenAuthorization) header: String,
                               @Body registerDevice : RegisterDeviceTask) :
            Response<Void>
}

object RegisterDeviceApi {
    val retrofitService : JsonPlaceHolderRegisterDeviceApi by lazy {
        retrofit.create(
            JsonPlaceHolderRegisterDeviceApi::class.java
        )
    }
}

//Update Mail
interface JsonPlaceHolderUpdateEmailApi {
    @Headers(MyHeaders.accept)
    @POST(ResponseUrls.updateEmail)
    suspend fun updateEmail(@Header (MyHeaders.tokenAuthorization) header: String,
                            @Body updateEmail: UpdateEmailTask) :
            Response<MailExistProperty>
}

object UpdateEmailApi {
    val retrofitService : JsonPlaceHolderUpdateEmailApi by lazy {
        retrofit.create(
            JsonPlaceHolderUpdateEmailApi::class.java
        )
    }
}

//Verify Email
interface JsonPlaceHolderVerifyMailApi {
    @Headers(MyHeaders.accept)
    @POST(ResponseUrls.verifyEmail)
    suspend fun verifyEmail(@Header (MyHeaders.tokenAuthorization) header: String,
                            @Body verifyEmail: VerifyEmailTask) :
            Response<ChangePhoneNumberOrEmailProperty>
}

object VerifyEmailApi {
    val retrofitService : JsonPlaceHolderVerifyMailApi by lazy {
        retrofit.create(
            JsonPlaceHolderVerifyMailApi::class.java
        )
    }
}

//Update Phone Number
interface JsonPlaceHolderUpdatePhoneNumberApi {
    @Headers(MyHeaders.accept)
    @POST(ResponseUrls.updatePhoneNumber)
    suspend fun updatePhoneNumber(@Header (MyHeaders.tokenAuthorization) header: String,
                                  @Body updatePhoneNumber: UpdatePhoneNumberTask) :
            Response<PhoneNumberExistProperty>
}

object UpdatePhoneNumberApi {
    val retrofitService : JsonPlaceHolderUpdatePhoneNumberApi by lazy {
        retrofit.create(
            JsonPlaceHolderUpdatePhoneNumberApi::class.java
        )
    }
}

//Verify Phone Number
interface JsonPlaceHolderVerifyPhoneNumberApi {
    @Headers(MyHeaders.accept)
    @POST(ResponseUrls.verifyPhoneNumber)
    suspend fun verifyPhoneNumber(@Header (MyHeaders.tokenAuthorization) header: String,
                                  @Body verifyPhoneNumber: VerifyPhoneNumberTask) :
            Response<ChangePhoneNumberOrEmailProperty>
}

object VerifyPhoneNumberApi {
    val retrofitService : JsonPlaceHolderVerifyPhoneNumberApi by lazy {
        retrofit.create(
            JsonPlaceHolderVerifyPhoneNumberApi::class.java
        )
    }
}