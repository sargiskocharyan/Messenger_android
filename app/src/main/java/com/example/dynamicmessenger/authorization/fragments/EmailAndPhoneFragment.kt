package com.example.dynamicmessenger.authorization.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.dynamicmessenger.authorization.viewModels.EmailAndPhoneViewModel
import com.example.dynamicmessenger.activitys.viewModels.MainActivityViewModel
import com.example.dynamicmessenger.databinding.FragmentEmailAndPhoneBinding
import com.example.dynamicmessenger.utils.Validations


class EmailAndPhoneFragment : Fragment() {
    private lateinit var viewModel: EmailAndPhoneViewModel
    private lateinit var binding: FragmentEmailAndPhoneBinding
    private val mainActivityViewModel: MainActivityViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEmailAndPhoneBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this).get(EmailAndPhoneViewModel::class.java)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        observers()

        binding.root.setOnClickListener {
            val view = requireActivity().currentFocus
            view?.let { v ->
                val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                imm?.hideSoftInputFromWindow(v.windowToken, 0)
            }
        }

        return binding.root
    }

    private fun observers() {
        viewModel.userEnteredEmail.observe(viewLifecycleOwner, Observer {
            viewModel.isEmailValid.value = Validations.isEmailValid(it)
            mainActivityViewModel.userMail = it
        })

        viewModel.userCode.observe(viewLifecycleOwner, Observer {
            mainActivityViewModel.userCode = it
        })

        viewModel.isEmailExists.observe(viewLifecycleOwner, Observer {
            mainActivityViewModel.userMailExists = it
        })
    }
}
