package com.example.dynamicmessenger.authorization.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.dynamicmessenger.R
import com.example.dynamicmessenger.authorization.viewModels.EmailAndPhoneViewModel
import com.example.dynamicmessenger.databinding.FragmentEmailAndPhoneBinding
import com.example.dynamicmessenger.utils.Validations


class EmailAndPhoneFragment : Fragment() {
    private lateinit var viewModel: EmailAndPhoneViewModel
    private lateinit var binding: FragmentEmailAndPhoneBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()TODO
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_email_and_phone, container, false)
        viewModel = ViewModelProvider(this).get(EmailAndPhoneViewModel::class.java)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        viewModel.userEnteredEmail.observe(viewLifecycleOwner, Observer {
            viewModel.hintVisibility.value = it.isNotEmpty()
            viewModel.isEmailValid.value = Validations.isEmailValid(it)
        })

        return binding.root
    }
}
