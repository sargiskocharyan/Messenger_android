package com.example.dynamicmessenger.userDataController.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.dynamicmessenger.network.authorization.models.Chat

@Database(entities = [SignedUser::class, UserToken::class, Chat::class], version = 1)
//@TypeConverters(DateTypeConverter::class)
abstract class SignedUserDatabase : RoomDatabase() {
    abstract fun signedUserDao(): SignedUserDao
    abstract fun userTokenDao(): UserTokenDao
    abstract fun userChatDao(): UserChatDao


    companion object {
        private var INSTANCE: SignedUserDatabase? = null

        fun getUserDatabase(context: Context): SignedUserDatabase? {
            return INSTANCE ?: synchronized(SignedUserDatabase::class){
                        Room.databaseBuilder(context.applicationContext,
                                SignedUserDatabase::class.java,
                        "signed_user_database")
                        .allowMainThreadQueries()
                        .build()
                }
        }

        fun destroyDataBase(){
            INSTANCE = null
        }
    }
}