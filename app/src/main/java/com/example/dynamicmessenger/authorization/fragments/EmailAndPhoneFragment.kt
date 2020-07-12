package com.example.dynamicmessenger.authorization.fragments

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.dynamicmessenger.R
import com.example.dynamicmessenger.authorization.viewModels.EmailAndPhoneViewModel
import com.example.dynamicmessenger.databinding.FragmentEmailAndPhoneBinding
import com.example.dynamicmessenger.network.authorization.models.EmailExistTask
import com.example.dynamicmessenger.utils.Validations
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


class EmailAndPhoneFragment : Fragment() {
    private lateinit var viewModel: EmailAndPhoneViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()TODO
        val binding: FragmentEmailAndPhoneBinding =
            FragmentEmailAndPhoneBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this).get(EmailAndPhoneViewModel::class.java)

        binding.loginRegisterEmailEditText.addTextChangedListener(object : TextWatcher {
            @SuppressLint("ResourceAsColor")
            override fun afterTextChanged(s: Editable?) {
                if (Validations.isEmailValid(binding.loginRegisterEmailEditText.text.toString())) {
                    binding.continueButton.isEnabled = true
                    binding.emailHintTextView.setTextColor(Color.BLUE)
                    binding.loginRegisterEmailEditText.setBackgroundResource(R.drawable.edittext_blue_underline)
                    binding.continueButton.setBackgroundResource(R.drawable.enable_button_design)
                } else {
                    binding.continueButton.isEnabled = false
                    binding.emailHintTextView.setTextColor(Color.RED)
                    binding.loginRegisterEmailEditText.setBackgroundResource(R.drawable.edittext_red_underline)
                    binding.continueButton.setBackgroundResource(R.drawable.disable_button_design)
                }
                if (binding.loginRegisterEmailEditText.text.toString().isNotEmpty()) {
                    binding.emailHintTextView.visibility = View.VISIBLE
                } else {
                    binding.emailHintTextView.visibility = View.INVISIBLE
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.continueButton.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            if (Validations.isEmailValid(binding.loginRegisterEmailEditText.text.toString())) {
                binding.progressBar.visibility = View.VISIBLE
                val emailExist = EmailExistTask(binding.loginRegisterEmailEditText.text.toString())
                viewModel.emailNetwork(it, emailExist, context, binding)
            } else {
                binding.progressBar.visibility = View.INVISIBLE
                Toast.makeText(context, "Invalid email", Toast.LENGTH_SHORT).show()
            }
        }
        return binding.root
    }
}

