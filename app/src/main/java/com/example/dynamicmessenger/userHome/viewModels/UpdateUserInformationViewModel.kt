package com.example.dynamicmessenger.userHome.viewModels

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dynamicmessenger.common.SharedConfigs
import com.example.dynamicmessenger.network.authorization.UniversityApi
import com.example.dynamicmessenger.network.authorization.UpdateUserApi
import com.example.dynamicmessenger.network.authorization.models.UniversityProperty
import com.example.dynamicmessenger.network.authorization.models.UpdateUserTask
import com.example.dynamicmessenger.network.authorization.models.User
import com.example.dynamicmessenger.userDataController.SaveToken
import com.example.dynamicmessenger.userDataController.SharedPreferencesManager
import com.example.dynamicmessenger.utils.MyAlertMessage
import kotlinx.coroutines.launch

class UpdateUserInformationViewModel: ViewModel() {

    fun updateUserNetwork(updateUserTask: UpdateUserTask, context: Context?, closure: (Boolean) -> Unit) {
        val myEncryptedToken = SharedPreferencesManager.getUserToken(context!!)
        val myToken = SaveToken.decrypt(myEncryptedToken)
        viewModelScope.launch {
            try {
                val response = UpdateUserApi.retrofitService.updateUserResponseAsync(myToken!! ,updateUserTask)
                if (response.isSuccessful) {
                    val user = User(response.body()!!.name,
                        response.body()!!.lastname,
                        response.body()!!._id,
                        response.body()!!.email,
                        response.body()!!.username,
                        response.body()!!.university,
                        response.body()!!.avatarURL
                    )
                    SharedConfigs.signedUser = user
                    SharedPreferencesManager.saveUserObject(context,user)
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
        val myToken = SharedPreferencesManager.getUserToken(context!!)
        val token = SaveToken.decrypt(myToken)
        viewModelScope.launch {
            try {
                val response = UniversityApi.retrofitService.universityResponseAsync(token!!)
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