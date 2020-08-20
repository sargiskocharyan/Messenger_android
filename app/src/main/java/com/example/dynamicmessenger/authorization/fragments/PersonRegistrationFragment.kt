package com.example.dynamicmessenger.authorization.fragments


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.dynamicmessenger.R
import com.example.dynamicmessenger.authorization.viewModels.PersonRegistrationViewModel
import com.example.dynamicmessenger.databinding.FragmentPersonRegistrationBinding
import com.example.dynamicmessenger.utils.Validations


class PersonRegistrationFragment : Fragment() {

    private lateinit var viewModel: PersonRegistrationViewModel
    private lateinit var binding: FragmentPersonRegistrationBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPersonRegistrationBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this).get(PersonRegistrationViewModel::class.java)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        setValidations()

        val adapter = ArrayAdapter.createFromResource(requireContext(), R.array.genders, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        (binding.registrationGenderSpinner.editText as? AutoCompleteTextView)?.setAdapter(adapter)

        binding.root.setOnClickListener {
            val view = requireActivity().currentFocus
            view?.let { v ->
                val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                imm?.hideSoftInputFromWindow(v.windowToken, 0)
            }
        }

        return binding.root
    }

    private fun setValidations() {
        viewModel.userEnteredUsername.observe(viewLifecycleOwner, Observer {
            if (Validations.isUsernameValid(it)) {
                viewModel.checkUsernameExists().observe(viewLifecycleOwner, Observer { isUsernameFree ->
                    if (isUsernameFree) {
                        viewModel.isValidUsername.value = true
                    } else {
                        viewModel.isValidUsername.value = null
                    }
                })
            } else {
                viewModel.isValidUsername.value = false
            }
            viewModel.changeIsValidParameters()
        })

        viewModel.userEnteredName.observe(viewLifecycleOwner, Observer {
            viewModel.isValidName.value = Validations.isNameValid(it) || it.isEmpty()
            viewModel.changeIsValidParameters()
        })

        viewModel.userEnteredLastName.observe(viewLifecycleOwner, Observer {
            viewModel.isValidLastName.value = Validations.isLastNameValid(it) || it.isEmpty()
            viewModel.changeIsValidParameters()
        })
    }
}

