package com.example.dynamicmessenger.userHome.viewModels

import android.app.Application
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.dynamicmessenger.common.SharedConfigs
import com.example.dynamicmessenger.network.UniversityApi
import com.example.dynamicmessenger.network.UpdateUserApi
import com.example.dynamicmessenger.network.authorization.models.UniversityProperty
import com.example.dynamicmessenger.network.authorization.models.UpdateUserTask
import com.example.dynamicmessenger.userDataController.database.SignedUser
import com.example.dynamicmessenger.userDataController.database.SignedUserDatabase
import com.example.dynamicmessenger.userDataController.database.UserTokenRepository
import com.example.dynamicmessenger.utils.MyAlertMessage
import kotlinx.coroutines.launch

class UpdateUserInformationViewModel(application: Application) : AndroidViewModel(application) {
    private val tokenDao = SignedUserDatabase.getUserDatabase(application)!!.userTokenDao()
    private val tokenRep = UserTokenRepository(tokenDao)
    fun updateUserNetwork(updateUserTask: UpdateUserTask, context: Context?, closure: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val response = UpdateUserApi.retrofitService.updateUserResponseAsync(SharedConfigs.token!! ,updateUserTask)
                if (response.isSuccessful) {
                    val user = SignedUser(
                        response.body()!!._id,
                        response.body()!!.name,
                        response.body()!!.lastname,
                        response.body()!!.username,
                        response.body()!!.email,
                        response.body()!!.university,
                        response.body()!!.avatarURL
                    )
                    SharedConfigs.signedUser = user
//                    SharedPreferencesManager.saveUserObject(context!!,user)
                    closure(true)
                } else {
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
                val response = UniversityApi.retrofitService.universityResponseAsync(SharedConfigs.token!!)
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
}