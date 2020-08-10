package com.example.dynamicmessenger.userDataController.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.dynamicmessenger.network.authorization.models.Chat
import com.example.dynamicmessenger.network.authorization.models.User

@Dao
interface SignedUserDao {
    @Query("SELECT * from signed_user")
    fun getSignedUser(): SignedUser

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun update(signedUser: SignedUser)

    @Query("DELETE FROM signed_user")
    fun deleteAll()
}

@Dao
interface UserTokenDao {
    @Query("SELECT * FROM user_token")
    fun getUserToken(): UserToken?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun update(token: UserToken)

    @Query("DELETE FROM user_token")
    fun deleteAll()
}

@Dao
interface UserCallsDao {
    @Query("SELECT * FROM user_calls ORDER BY time DESC")
    fun getUserCalls(): LiveData<List<UserCalls>>

    @Query("SELECT * FROM user_calls ORDER BY time DESC LIMIT 1")
    fun getLastCall(): UserCalls?

    @Query("SELECT * FROM user_calls WHERE time = :time")
    fun getCallByTime(time: Long): UserCalls?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(userCalls: UserCalls)

    @Query("DELETE FROM user_calls WHERE time = :time")
    suspend fun deleteCallByTime(time: Long)

    @Query("DELETE FROM user_calls")
    suspend fun deleteAll()
}

@Dao
interface SavedUserDao {
    @Query("SELECT * FROM user WHERE _id = :id")
    fun getUserById(id: String): User?

    @Query("SELECT * FROM user")
    fun getAllUsers(): List<User>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(userCalls: User)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(userCalls: List<User>)

    @Query("DELETE FROM user WHERE _id = :id")
    suspend fun deleteUserById(id: String)

    @Query("DELETE FROM user")
    suspend fun deleteAll()
}

@Dao
interface UserChatsDao {
    @Query("SELECT * FROM chat")
    fun getAllChats(): List<Chat>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(chats: List<Chat>)

    @Query("DELETE FROM chat")
    suspend fun deleteAll()
}

@Dao
interface UserContactsDao {
    @Query("SELECT * FROM contacts")
    fun getAllContacts(): List<Contacts>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(contact: List<Contacts>)

    @Query("DELETE FROM contacts")
    suspend fun deleteAll()
}