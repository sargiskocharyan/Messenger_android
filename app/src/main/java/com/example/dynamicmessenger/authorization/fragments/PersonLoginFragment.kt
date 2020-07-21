package com.example.dynamicmessenger.authorization.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.dynamicmessenger.activitys.HomeActivity
import com.example.dynamicmessenger.activitys.MainActivity
import com.example.dynamicmessenger.authorization.viewModels.PersonLoginViewModel
import com.example.dynamicmessenger.databinding.FragmentPersonLoginBinding

class PersonLoginFragment : Fragment() {

    private lateinit var viewModel: PersonLoginViewModel
    private lateinit var binding: FragmentPersonLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
        binding = FragmentPersonLoginBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this).get(PersonLoginViewModel::class.java)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        val isEmailExists = MainActivity.userMailExists ?: false
        val personEmail = MainActivity.userMail ?: ""
        val personCode = MainActivity.userCode
        viewModel.personEmail = personEmail

        viewModel.isEmailExist.value = isEmailExists
        viewModel.userEnteredCode.value = personCode

        viewModel.userEnteredCode.observe(viewLifecycleOwner, Observer {
            viewModel.hintVisibility.value = it.isNotEmpty()
            viewModel.isCodeValid.value = it.length == 4
        })

        binding.button.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            viewModel.loginNetwork(it) { closure ->
                if (closure) {
                    val intent = Intent(requireActivity(), HomeActivity::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                    (activity as Activity?)!!.overridePendingTransition(1, 1)
                }
            }
        }

        return binding.root
    }
}
