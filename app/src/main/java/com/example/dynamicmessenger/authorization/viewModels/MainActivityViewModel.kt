package com.example.dynamicmessenger.authorization.viewModels

import androidx.lifecycle.ViewModel

class MainActivityViewModel: ViewModel() {
    var userMailExists: Boolean? = null
    var userCode: String? = null
    var userMail: String? = null
}