package com.example.dynamicmessenger.authorization.viewModels

import android.content.Context
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import com.example.dynamicmessenger.R
import com.example.dynamicmessenger.network.authorization.UniversityApi
import com.example.dynamicmessenger.network.authorization.UpdateUserApi
import com.example.dynamicmessenger.network.authorization.models.UniversityProperty
import com.example.dynamicmessenger.network.authorization.models.UpdateUserTask
import com.example.dynamicmessenger.network.authorization.models.User
import com.example.dynamicmessenger.userDataController.SaveToken
import com.example.dynamicmessenger.userDataController.SharedPreferencesManager
import com.example.dynamicmessenger.utils.MyAlertMessage
import kotlinx.coroutines.launch

class PersonRegistrationViewModel: ViewModel(){

    fun updateUserNetwork(view: View, updateUserTask: UpdateUserTask, context: Context?) {
        val myEncryptedToken = SharedPreferencesManager.getUserToken(context!!)
        val myToken = SaveToken.decrypt(myEncryptedToken)
        viewModelScope.launch {
            try {
                val response = UpdateUserApi.retrofitService.updateUserResponseAsync(myToken!! ,updateUserTask).await()
                if (response.isSuccessful) {
                    val user = User(response.body()!!.name,
                        response.body()!!.lastname,
                        response.body()!!._id,
                        response.body()!!.email,
                        response.body()!!.username,
                        response.body()!!.university
                    )
                    SharedPreferencesManager.saveUserObject(context,user)
                    view.findNavController().navigate(R.id.action_personRegistrationFragment_to_finishRegistrationFragment)
                } else {
                    MyAlertMessage.showAlertDialog(context, "Enter correct email")
                }
            } catch (e: Exception) {
                MyAlertMessage.showAlertDialog(context, "Check your internet connection")
            }
        }
    }

    fun getAllUniversity(context: Context?, closure: (List<UniversityProperty>) -> Unit){
        val myToken = SharedPreferencesManager.getUserToken(context!!)
        val token = SaveToken.decrypt(myToken)
        var allUniversity: List<UniversityProperty>? = null
        viewModelScope.launch {
            try {
                val response = UniversityApi.retrofitService.universityResponseAsync(token!!).await()
                if (response.isSuccessful) {
                    allUniversity = response.body()
                    closure(allUniversity!!)
                } else {
                    MyAlertMessage.showAlertDialog(context, "Get all university error")
                }
            } catch (e: Exception) {
                MyAlertMessage.showAlertDialog(context, "Check your internet connection")
            }
        }
    }
}