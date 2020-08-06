package com.example.dynamicmessenger.authorization.fragments


import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.dynamicmessenger.R
import com.example.dynamicmessenger.authorization.viewModels.PersonRegistrationViewModel
import com.example.dynamicmessenger.databinding.FragmentPersonRegistrationBinding
import com.example.dynamicmessenger.network.authorization.models.UniversityProperty
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

        viewModel.userEnteredUsername.observe(viewLifecycleOwner, Observer {
            if (Validations.isUsernameValid(it)) {
                viewModel.checkUsernameExists()
                viewModel.isValidUsername.value = true
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

        val adapter = ArrayAdapter.createFromResource(requireContext(), R.array.genders, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.registrationGenderSpinner.adapter = adapter
        binding.registrationGenderSpinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                when (position) {
                    1 -> viewModel.userEnteredGender.value = "male"
                    2 -> viewModel.userEnteredGender.value = "female"
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        return binding.root
    }
}

