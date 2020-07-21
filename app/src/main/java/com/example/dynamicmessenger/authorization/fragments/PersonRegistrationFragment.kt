package com.example.dynamicmessenger.authorization.fragments


import android.annotation.SuppressLint
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
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.dynamicmessenger.R
import com.example.dynamicmessenger.authorization.viewModels.PersonRegistrationViewModel
import com.example.dynamicmessenger.databinding.FragmentPersonRegistrationBinding
import com.example.dynamicmessenger.network.authorization.models.UniversityProperty
import com.example.dynamicmessenger.network.authorization.models.UpdateUserTask
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

        binding.usernameEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (Validations.isUsernameValid(binding.usernameEditText.text.toString())
                    && Validations.isNameValid(binding.nameEditText.text.toString())
                    && Validations.isLastNameValid(binding.lastNameEditText.text.toString())
                ) {
                    viewModel.isValidParameters.value = true
                } else {
                    viewModel.isValidParameters.value = true
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        var allUniversity: List<UniversityProperty>
        viewModel.getAllUniversity(requireContext()) {
            allUniversity = it
            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                allUniversity
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.universitySpinner.adapter = adapter
            binding.universitySpinner.onItemSelectedListener = object : OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    val allUniver = parent.selectedItem as UniversityProperty
                    viewModel.userEnteredUniversity.value = allUniver._id
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }

        return binding.root
    }
}

