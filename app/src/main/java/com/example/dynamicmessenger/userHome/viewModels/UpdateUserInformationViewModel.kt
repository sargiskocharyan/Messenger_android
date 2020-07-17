package com.example.dynamicmessenger.userHome.viewModels

import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.dynamicmessenger.common.SharedConfigs
import com.example.dynamicmessenger.network.*
import com.example.dynamicmessenger.network.authorization.models.UniversityProperty
import com.example.dynamicmessenger.network.authorization.models.UpdateUserTask
import com.example.dynamicmessenger.userDataController.database.SignedUser
import com.example.dynamicmessenger.utils.MyAlertMessage
import kotlinx.coroutines.launch

class UpdateUserInformationViewModel(application: Application) : AndroidViewModel(application) {
    fun updateUserNetwork(updateUserTask: UpdateUserTask, context: Context?, closure: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val response = UpdateUserApi.retrofitService.updateUserResponseAsync(SharedConfigs.token ,updateUserTask)
                if (response.isSuccessful) {
                    val user = SignedUser(
                        response.body()!!._id,
                        response.body()!!.name,
                        response.body()!!.lastname,
                        response.body()!!.username,
                        response.body()!!.email,
//                        response.body()!!.university, TODO
                        null,
                        response.body()!!.avatarURL
                    )
                    SharedConfigs.signedUser = user
                    closure(true)
                } else {
                    Log.i("+++", "update user else $response")
                    MyAlertMessage.showAlertDialog(context, "Enter correct email")
                }
            } catch (e: Exception) {
                MyAlertMessage.showAlertDialog(context, "Check your internet connection")
            }
        }
    }

    fun getAllUniversity(context: Context?, closure: (List<UniversityProperty>) -> Unit) {
        viewModelScope.launch {
            try {
                val response = UniversityApi.retrofitService.universityResponseAsync(SharedConfigs.token)
                if (response.isSuccessful) {
                    closure(response.body()!!)
                } else {
                    Toast.makeText(context, "Cant get university's name", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                MyAlertMessage.showAlertDialog(context, "Check your internet connection")
            }
        }
    }

    fun deactivateUserAccount(closure: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val response = DeactivateUserApi.retrofitService.deactivateUserResponseAsync(SharedConfigs.token)
                if (response.isSuccessful) {
                    closure(true)
                }
            } catch (e: Exception) {

            }
        }
    }

    fun deleteUserAccount(closure: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val response = DeleteUserApi.retrofitService.deleteUserResponseAsync(SharedConfigs.token)
                Log.i("+++", "delete user $response")
                if (response.isSuccessful) {
                    closure(true)
                }
            } catch (e: Exception) {
                Log.i("+++", "delete user exception $e")
            }
        }
    }
}