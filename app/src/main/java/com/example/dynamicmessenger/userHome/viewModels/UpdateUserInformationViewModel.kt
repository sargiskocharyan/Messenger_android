package com.example.dynamicmessenger.userHome.viewModels

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.navigation.findNavController
import com.example.dynamicmessenger.R
import com.example.dynamicmessenger.network.authorization.UniversityApi
import com.example.dynamicmessenger.network.authorization.UpdateUserApi
import com.example.dynamicmessenger.network.authorization.models.UniversityProperty
import com.example.dynamicmessenger.network.authorization.models.UpdateUserProperty
import com.example.dynamicmessenger.network.authorization.models.UpdateUserTask
import com.example.dynamicmessenger.network.authorization.models.User
import com.example.dynamicmessenger.userDataController.SaveToken
import com.example.dynamicmessenger.userDataController.SharedPreferencesManager
import com.example.dynamicmessenger.userHome.fragments.UserInformationFragment
import com.example.dynamicmessenger.utils.MyAlertMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UpdateUserInformationViewModel: ViewModel() {

    fun updateUserNetwork(view: View, updateUserTask: UpdateUserTask, context: Context?, closure: (Boolean) -> Unit) {
        val myEncryptedToken = SharedPreferencesManager.getUserToken(context!!)
        val myToken = SaveToken.decrypt(myEncryptedToken)
        val getProperties: Call<UpdateUserProperty> = UpdateUserApi.retrofitService.updateUserResponse(myToken!! ,updateUserTask)
        try {
            getProperties.enqueue(object : Callback<UpdateUserProperty?> {
                override fun onResponse(
                    call: Call<UpdateUserProperty?>,
                    response: Response<UpdateUserProperty?>
                ) {
                    if (response.isSuccessful) {
                        val user = User(response.body()!!.name,
                            response.body()!!.lastname,
                            response.body()!!._id,
                            response.body()!!.email,
                            response.body()!!.username,
                            response.body()!!.university
                        )
                        SharedPreferencesManager.saveUserObject(context,user)
                        closure(true)
                    } else {
                        MyAlertMessage.showAlertDialog(context, "Enter correct email")
                    }
                }
                override fun onFailure(
                    call: Call<UpdateUserProperty?>,
                    t: Throwable
                ) {
                    MyAlertMessage.showAlertDialog(context, "Check your internet connection")
                }
            })
        } catch (e: Exception) {
            Toast.makeText(context, "Something gone a wrong", Toast.LENGTH_SHORT).show()
        }
    }

    fun getAllUniversity(context: Context?, closure: (List<UniversityProperty>) -> Unit) {
        val myToken = SharedPreferencesManager.getUserToken(context!!)
        val token = SaveToken.decrypt(myToken)
        val getProperties: Call<List<UniversityProperty>> = UniversityApi.retrofitService.universityResponse(token!!)
        var allUniversity: List<UniversityProperty>? = null
        try {
            getProperties.enqueue(object : Callback<List<UniversityProperty>> {
                override fun onResponse(
                    call: Call<List<UniversityProperty>>,
                    response: Response<List<UniversityProperty>>
                ) {
                    if (response.isSuccessful) {
                        allUniversity = response.body()
                        closure(allUniversity!!)
                    } else {
                        Toast.makeText(context, "Cant get university's name", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onFailure(
                    call: Call<List<UniversityProperty>>,
                    t: Throwable
                ) {
                    MyAlertMessage.showAlertDialog(context, "Check your internet connection")
                }
            })
        } catch (e: Exception) {
            Toast.makeText(context, "Something gone a wrong", Toast.LENGTH_SHORT).show()
        }
    }
}