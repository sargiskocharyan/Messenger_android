package com.example.dynamicmessenger.userHome.viewModels

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.databinding.PropertyChangeRegistry
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.dynamicmessenger.common.SharedConfigs
import com.example.dynamicmessenger.network.*
import com.example.dynamicmessenger.network.authorization.models.UniversityProperty
import com.example.dynamicmessenger.network.authorization.models.UpdateUserTask
import com.example.dynamicmessenger.userDataController.database.SignedUser
import com.example.dynamicmessenger.utils.ClassConverter
import com.example.dynamicmessenger.utils.DatePickerHelper
import com.example.dynamicmessenger.utils.MyAlertMessage
import kotlinx.coroutines.launch
import java.util.*

class UpdateUserInformationViewModel(application: Application) : AndroidViewModel(application), Observable {
    private var datePicker = DatePickerHelper(application, true)

    @Bindable
    val userEnteredUsername = MutableLiveData<String>()
    @Bindable
    val userEnteredName = MutableLiveData<String>()
    @Bindable
    val userEnteredLastName = MutableLiveData<String>()
    @Bindable
    val userEnteredInfo = MutableLiveData<String>()
    @Bindable
    val userEnteredPhoneNumber = MutableLiveData<String>()
    @Bindable
    val userEnteredEmail = MutableLiveData<String>()

    private val _userEnteredDate = MutableLiveData<String>()
    val userEnteredDate: LiveData<String> = _userEnteredDate

    fun updateUserNetwork(updateUserTask: UpdateUserTask, context: Context?, closure: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val response = UpdateUserApi.retrofitService.updateUserResponseAsync(SharedConfigs.token ,updateUserTask)
                if (response.isSuccessful) {
                    SharedConfigs.signedUser = ClassConverter.userToSignedUser(response.body()!!)
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
                if (response.isSuccessful) {
                    closure(true)
                }
            } catch (e: Exception) {
                Log.i("+++", "delete user exception $e")
            }
        }
    }

    fun showDatePickerDialog() {//TODO
        val cal = Calendar.getInstance()
        val d = cal.get(Calendar.DAY_OF_MONTH)
        val m = cal.get(Calendar.MONTH)
        val y = cal.get(Calendar.YEAR)
        datePicker.showDialog(d, m, y, object : DatePickerHelper.Callback {
            @SuppressLint("SetTextI18n")
            override fun onDateSelected(dayOfMonth: Int, month: Int, year: Int) {
                val dayStr = if (dayOfMonth < 10) "0${dayOfMonth}" else "$dayOfMonth"
                val mon = month + 1
                val monthStr = if (mon < 10) "0${mon}" else "$mon"
                _userEnteredDate.value = "$monthStr/$dayStr/$year"
            }
        })
    }

    private val callbacks: PropertyChangeRegistry by lazy { PropertyChangeRegistry() }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback) {
        callbacks.add(callback)
    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback) {
        callbacks.remove(callback)
    }
}