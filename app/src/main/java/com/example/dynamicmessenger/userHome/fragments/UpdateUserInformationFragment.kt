package com.example.dynamicmessenger.userHome.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.dynamicmessenger.R
import com.example.dynamicmessenger.databinding.FragmentUpdateUserInformationBinding
import com.example.dynamicmessenger.network.authorization.models.UniversityProperty
import com.example.dynamicmessenger.network.authorization.models.UpdateUserTask
import com.example.dynamicmessenger.userHome.viewModels.UpdateUserInformationViewModel
import com.example.dynamicmessenger.utils.Validations

class UpdateUserInformationFragment : Fragment() {
    private lateinit var viewModel: UpdateUserInformationViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentUpdateUserInformationBinding =
                        DataBindingUtil.inflate(inflater,
                            R.layout.fragment_update_user_information,
                            container,false)
        viewModel = ViewModelProvider(this).get(UpdateUserInformationViewModel::class.java)
        binding.lifecycleOwner = this
        binding.updateUserViewModel = viewModel
        binding.editTextUsername.addTextChangedListener(object : TextWatcher {
            @SuppressLint("ResourceAsColor")
            override fun afterTextChanged(s: Editable?) {
                if (Validations.isUsernameValid(binding.editTextUsername.text.toString())
                    && Validations.isNameValid(binding.editTextName.text.toString())
                    && Validations.isLastNameValid(binding.editTextLastname.text.toString())) {
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
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
        var allUniversity: List<UniversityProperty>
        viewModel.getAllUniversity(requireContext()){
            allUniversity = it
            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                allUniversity
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerUniversity.adapter = adapter
            binding.spinnerUniversity.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
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
            val updateUserTask = UpdateUserTask(name, lastname, username, university)
            viewModel.updateUserNetwork(updateUserTask, context){closure ->
                if (closure) {
                    val selectedFragment = UserInformationFragment()
                    activity?.supportFragmentManager?.beginTransaction()?.replace(
                        R.id.fragmentContainer,
                        selectedFragment
                    )?.commit()
                }
            }
        }

        binding.updateUserBackImageView.setOnClickListener {
            val selectedFragment = UserInformationFragment()
            activity?.supportFragmentManager?.beginTransaction()?.replace(
                R.id.fragmentContainer,
                selectedFragment
            )?.commit()
        }

        return binding.root
    }
}