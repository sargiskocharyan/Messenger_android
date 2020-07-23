package com.example.dynamicmessenger.userDataController.database

import androidx.room.*
import com.example.dynamicmessenger.network.authorization.models.Chat

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
    fun getUserCalls(): List<UserCalls?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(userCalls: UserCalls)

    @Query("DELETE FROM user_calls")
    fun deleteAll()
}