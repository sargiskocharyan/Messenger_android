package com.example.dynamicmessenger.userDataController.database

import android.content.Context
import androidx.room.*
import com.example.dynamicmessenger.network.authorization.models.Chat
import com.example.dynamicmessenger.network.authorization.models.User
import com.google.gson.Gson

import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type


@Database(entities = [SignedUser::class, UserToken::class, UserCalls::class, User::class, Chat::class, Contacts::class], version = 1)
@TypeConverters(Converters::class)
abstract class SignedUserDatabase : RoomDatabase() {
    abstract fun signedUserDao(): SignedUserDao
    abstract fun userTokenDao(): UserTokenDao
    abstract fun userCallsDao(): UserCallsDao
    abstract fun savedUserDao(): SavedUserDao
    abstract fun userChatsDao(): UserChatsDao
    abstract fun userContactsDao(): UserContactsDao


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

class Converters {
    @TypeConverter
    fun fromString(value: String?): ArrayList<String> {
        val listType: Type = object : TypeToken<ArrayList<String?>?>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromArrayList(list: ArrayList<String?>?): String {
        val gson = Gson()
        return gson.toJson(list)
    }

    @TypeConverter
    fun fromStringList(string: List<String?>?): String? {
        if (string == null) {
            return null
        }
        val gson = Gson()
        val type = object : TypeToken<List<String?>?>() {}.type
        return gson.toJson(string, type)
    }

    @TypeConverter
    fun toStringList(string: String?): List<String>? {
        if (string == null) {
            return null
        }
        val gson = Gson()
        val type = object : TypeToken<List<String?>?>() {}.type
        return gson.fromJson<List<String>>(string, type)
    }
}