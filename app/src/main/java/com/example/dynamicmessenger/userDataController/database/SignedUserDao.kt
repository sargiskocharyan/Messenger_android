package com.example.dynamicmessenger.userDataController.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface SignedUserDao {
    @Query("SELECT * from signed_user")
    fun getSignedUser(): LiveData<SignedUser>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(signedUser: SignedUser)

    @Query("DELETE FROM signed_user")
    fun deleteAll()
}

@Dao
interface UserTokenDao {
    @Query("SELECT * FROM user_token")
    fun getUserToken(): UserToken

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(token: UserToken)

    @Query("DELETE FROM user_token")
    fun deleteAll()
}
