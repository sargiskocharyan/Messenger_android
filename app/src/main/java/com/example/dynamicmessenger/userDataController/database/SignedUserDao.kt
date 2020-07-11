package com.example.dynamicmessenger.userDataController.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.dynamicmessenger.network.authorization.models.Chat

@Dao
interface SignedUserDao {
    @Query("SELECT * from signed_user")
    fun getSignedUser(): SignedUser

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(signedUser: SignedUser)

    @Query("DELETE FROM signed_user")
    fun deleteAll()
}

@Dao
interface UserTokenDao {
    @Query("SELECT * FROM user_token")
    fun getUserToken(): UserToken?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(token: UserToken)

    @Query("DELETE FROM user_token")
    fun deleteAll()
}
