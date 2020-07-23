package com.example.dynamicmessenger.authorization.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.dynamicmessenger.R
import com.example.dynamicmessenger.authorization.viewModels.EmailAndPhoneViewModel
import com.example.dynamicmessenger.authorization.viewModels.MainActivityViewModel
import com.example.dynamicmessenger.databinding.FragmentEmailAndPhoneBinding
import com.example.dynamicmessenger.utils.Validations


class EmailAndPhoneFragment : Fragment() {
    private lateinit var viewModel: EmailAndPhoneViewModel
    private lateinit var binding: FragmentEmailAndPhoneBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEmailAndPhoneBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this).get(EmailAndPhoneViewModel::class.java)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        val activityViewModel: MainActivityViewModel by activityViewModels()

        viewModel.userEnteredEmail.observe(viewLifecycleOwner, Observer {
            viewModel.hintVisibility.value = it.isNotEmpty()
            viewModel.isEmailValid.value = Validations.isEmailValid(it)
            activityViewModel.userMail = it
        })

        viewModel.userCode.observe(viewLifecycleOwner, Observer {
            activityViewModel.userCode = it
        })

        viewModel.isEmailExists.observe(viewLifecycleOwner, Observer  {
            activityViewModel.userMailExists = it
        })

        return binding.root
    }
}
