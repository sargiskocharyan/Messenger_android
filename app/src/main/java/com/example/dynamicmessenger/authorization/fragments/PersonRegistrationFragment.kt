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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentPersonRegistrationBinding =
            FragmentPersonRegistrationBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this).get(PersonRegistrationViewModel::class.java)

        binding.editTextUsername.addTextChangedListener(object : TextWatcher {
            @SuppressLint("ResourceAsColor")
            override fun afterTextChanged(s: Editable?) {
                if (Validations.isUsernameValid(binding.editTextUsername.text.toString())
                    && Validations.isNameValid(binding.editTextName.text.toString())
                    && Validations.isLastNameValid(binding.editTextLastname.text.toString())
                ) {
                    binding.continueButton.isEnabled = true
                    binding.continueButton.setBackgroundResource(R.drawable.enable_button_design)
                } else {
                    binding.continueButton.isEnabled = false
                    binding.continueButton.setBackgroundResource(R.drawable.disable_button_design)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        var university: String = ""
//        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
        var allUniversity: List<UniversityProperty>
        viewModel.getAllUniversity(requireContext()) {
            allUniversity = it
            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                allUniversity
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerUniversity.adapter = adapter
            binding.spinnerUniversity.onItemSelectedListener = object : OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    val allUniver = parent.selectedItem as UniversityProperty
                    university = allUniver._id
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }


        binding.continueButton.setOnClickListener {
            val name = binding.editTextName.text.toString()
            val lastname = binding.editTextLastname.text.toString()
            val username = binding.editTextUsername.text.toString()
            val updateUserTask =
                UpdateUserTask(name, lastname, username, university, null, null, null, null, null)
            viewModel.updateUserNetwork(it, updateUserTask, context)
        }

        binding.skipButton.setOnClickListener {
            it.findNavController()
                .navigate(R.id.action_personRegistrationFragment_to_finishRegistrationFragment)
        }
        return binding.root
    }
}

