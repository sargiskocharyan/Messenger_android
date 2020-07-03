package com.example.dynamicmessenger.userDataController.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [SignedUser::class, UserToken::class], version = 1)
//@TypeConverters(DateTypeConverter::class)
abstract class SignedUserDatabase : RoomDatabase() {
    abstract fun signedUserDao(): SignedUserDao
    abstract fun userTokenDao(): UserTokenDao


    companion object {
        private var INSTANCE: SignedUserDatabase? = null

        fun getSignedUserDatabase(context: Context): SignedUserDatabase? {
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