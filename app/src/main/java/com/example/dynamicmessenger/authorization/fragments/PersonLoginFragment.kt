package com.example.dynamicmessenger.authorization.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.dynamicmessenger.R
import com.example.dynamicmessenger.activitys.HomeActivity
import com.example.dynamicmessenger.activitys.MainActivity
import com.example.dynamicmessenger.authorization.viewModels.PersonLoginViewModel
import com.example.dynamicmessenger.databinding.FragmentPersonLoginBinding
import com.example.dynamicmessenger.network.authorization.models.EmailExistTask
import com.example.dynamicmessenger.network.authorization.models.LoginTask
import com.example.dynamicmessenger.userDataController.SharedPreferencesManager
import com.example.dynamicmessenger.userDataController.database.SignedUserDatabase
import com.example.dynamicmessenger.userDataController.database.UserTokenRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class PersonLoginFragment : Fragment() {

    private lateinit var viewModel: PersonLoginViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
        val binding: FragmentPersonLoginBinding =
            FragmentPersonLoginBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this).get(PersonLoginViewModel::class.java)
        val isEmailExists = MainActivity.userMailExists ?: false
        val personEmail = MainActivity.userMail ?: ""
        val personCode = MainActivity.userCode

        binding.verificationCode.setText(personCode)

        if (binding.verificationCode.text.length != 4) {
            binding.button.isEnabled = false
            binding.button.setBackgroundResource(R.drawable.disable_button_design)
        }
        binding.verificationCode.addTextChangedListener(object : TextWatcher {
            @SuppressLint("ResourceAsColor")
            override fun afterTextChanged(s: Editable?) {
                if (binding.verificationCode.text.length == 4) {
                    binding.button.isEnabled = true
                    binding.verificationCodeHintTextView.setTextColor(Color.BLUE)
                    binding.verificationCode.setBackgroundResource(R.drawable.edittext_blue_underline)
                    binding.button.setBackgroundResource(R.drawable.enable_button_design)
                } else {
                    binding.button.isEnabled = false
                    binding.verificationCodeHintTextView.setTextColor(Color.RED)
                    binding.verificationCode.setBackgroundResource(R.drawable.edittext_red_underline)
                    binding.button.setBackgroundResource(R.drawable.disable_button_design)
                }
                if (binding.verificationCode.text.toString().isNotEmpty()) {
                    binding.verificationCodeHintTextView.visibility = View.VISIBLE
                } else {
                    binding.verificationCodeHintTextView.visibility = View.INVISIBLE
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        viewModel.loginRegisterVisibilityChange(isEmailExists, binding)
        binding.button.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            val loginTask = LoginTask(personEmail, binding.verificationCode.text.toString())
            viewModel.loginNetwork(it, isEmailExists, loginTask, binding) { closure ->
                if (closure) {
                    val intent = Intent(requireActivity(), HomeActivity::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                    (activity as Activity?)!!.overridePendingTransition(1, 1)
                }
            }
        }

        binding.resendVerificationCodeTextView.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            viewModel.emailNetwork(EmailExistTask(personEmail), binding)
        }
        return binding.root
    }
}
