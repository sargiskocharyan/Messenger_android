package com.example.dynamicmessenger.userHome.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import com.example.dynamicmessenger.R
import com.example.dynamicmessenger.activitys.MainActivity
import com.example.dynamicmessenger.common.SharedConfigs
import com.example.dynamicmessenger.databinding.FragmentUpdateUserInformationBinding
import com.example.dynamicmessenger.dialogs.DeactivateUserDialog
import com.example.dynamicmessenger.dialogs.DeleteUserDialog
import com.example.dynamicmessenger.network.authorization.models.UpdateUserTask
import com.example.dynamicmessenger.userDataController.SharedPreferencesManager
import com.example.dynamicmessenger.userHome.viewModels.UpdateUserInformationViewModel
import com.example.dynamicmessenger.utils.DatePickerHelper
import com.example.dynamicmessenger.utils.Validations
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.*

class UpdateUserInformationFragment : Fragment() {
    private lateinit var viewModel: UpdateUserInformationViewModel
    private lateinit var binding: FragmentUpdateUserInformationBinding
    private lateinit var datePicker: DatePickerHelper

    @SuppressLint("ResourceType")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUpdateUserInformationBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this).get(UpdateUserInformationViewModel::class.java)
        binding.lifecycleOwner = this
        binding.updateUserViewModel = viewModel
//        viewModel.userEnteredUsername.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
//            @SuppressLint("ResourceAsColor")
//                if (Validations.isUsernameValid(binding.usernameEditText.text.toString())
//                    && Validations.isNameValid(binding.nameEditText.text.toString())
//                    && Validations.isLastNameValid(binding.lastNameEditText.text.toString())) {
//                    binding.continueButton.isEnabled = true
//                    binding.continueButton.setBackgroundResource(R.drawable.enable_button_design)
//                } else {
//                    binding.continueButton.isEnabled = false
//                    binding.continueButton.setBackgroundResource(R.drawable.disable_button_design)
//                }
//        })

        //Bottom bar
        val bottomNavBar: BottomNavigationView = requireActivity().findViewById(R.id.bottomNavigationView)
        bottomNavBar.visibility = View.GONE
        datePicker = DatePickerHelper(requireContext(), true)

        //Toolbar
        setHasOptionsMenu(true)
        val toolbar: Toolbar = binding.updateUserInformationToolbar
        configureTopNavBar(toolbar)

        setValidations()

        var selectedGender = ""
        val adapter = ArrayAdapter.createFromResource(requireContext(), R.array.genders, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.genderSpinner.adapter = adapter
        (binding.genderTextField.editText as? AutoCompleteTextView)?.setAdapter(adapter)
        binding.genderSpinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                when (position) {
                    0 -> selectedGender = "male"
                    1 -> selectedGender = "female"
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        binding.birthDateDatePicker.editText?.setOnClickListener {
            showDatePickerDialog()
        }

        binding.continueButton.setOnClickListener {
            if (viewModel.userEnteredName.value?.isEmpty()!!) { viewModel.userEnteredName.value = null }
            if (viewModel.userEnteredLastName.value?.isEmpty()!!) { viewModel.userEnteredLastName.value = null }
            if (viewModel.userEnteredPhoneNumber.value?.isEmpty()!!) { viewModel.userEnteredPhoneNumber.value = null }
            val birthDate = viewModel.userEnteredDate.value
            val name = viewModel.userEnteredName.value
            val lastName = viewModel.userEnteredLastName.value
            val username = viewModel.userEnteredUsername.value
            val phoneNumber = viewModel.userEnteredPhoneNumber.value
            val userBio = viewModel.userEnteredInfo.value
            val updateUserTask = UpdateUserTask(name, lastName, username, phoneNumber, userBio, gender = selectedGender, birthday = birthDate)
            viewModel.updateUserNetwork(updateUserTask, context){closure ->
                if (closure) {
                    val selectedFragment = UserInformationFragment()
                    activity?.supportFragmentManager
                        ?.beginTransaction()
                        ?.replace(R.id.fragmentContainer, selectedFragment)
                        ?.addToBackStack(null)
                        ?.commit()
                }
            }
        }

        binding.deactivateAccountTextView.setOnClickListener {
            val deactivateUserAccountDialog = DeactivateUserDialog { userAnswer ->
                if (userAnswer) {
                    viewModel.deactivateUserAccount {
                        if (it) {
                            SharedPreferencesManager.deleteUserAllInformation(requireContext())
                            SharedConfigs.deleteToken()
                            SharedConfigs.deleteSignedUser()
                            val intent = Intent(activity, MainActivity::class.java)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            startActivity(intent)
                            (activity as Activity?)!!.overridePendingTransition(1, 1)
                        }
                    }
                }
            }
            deactivateUserAccountDialog.show(requireActivity().supportFragmentManager, "Dialog")
        }

        binding.deleteAccountTextView.setOnClickListener {
            val deleteUserAccountDialog = DeleteUserDialog { userAnswer ->
                if (userAnswer) {
                    viewModel.deleteUserAccount {
                        if (it) {
                            SharedPreferencesManager.deleteUserAllInformation(requireContext())
                            SharedConfigs.deleteToken()
                            SharedConfigs.deleteSignedUser()
                            val intent = Intent(activity, MainActivity::class.java)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            startActivity(intent)
                            (activity as Activity?)!!.overridePendingTransition(1, 1)
                        }
                    }
                }
            }
            deleteUserAccountDialog.show(requireActivity().supportFragmentManager, "Dialog")
        }

        binding.hidePersonalDataSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.hidePersonalData(isChecked)
        }

        return binding.root
    }
    private fun showDatePickerDialog() {
        val cal = Calendar.getInstance()
        val d = cal.get(Calendar.DAY_OF_MONTH)
        val m = cal.get(Calendar.MONTH)
        val y = cal.get(Calendar.YEAR)
        datePicker.showDialog(d, m, y, object : DatePickerHelper.Callback {
            @SuppressLint("SetTextI18n")
            override fun onDateSelected(dayOfMonth: Int, month: Int, year: Int) {
                val dayStr = if (dayOfMonth < 10) "0${dayOfMonth}" else "$dayOfMonth"
                val mon = month + 1
                val monthStr = if (mon < 10) "0${mon}" else "$mon"
                viewModel.userEnteredDate.value = "$monthStr/$dayStr/$year"
            }
        })
    }

    private fun configureTopNavBar(toolbar: Toolbar) {
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    private fun setValidations() {
        viewModel.userEnteredEmail.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            viewModel.isEmailValid.value = Validations.isEmailValid(it) || it.isEmpty()
            viewModel.changeIsValidParameters()
        })

        viewModel.userEnteredUsername.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            viewModel.isUsernameValid.value = Validations.isNameValid(it)
            if (Validations.isUsernameValid(it)) {
                if (it.toLowerCase() == SharedConfigs.signedUser?.username?.toLowerCase()) {
                    viewModel.isUsernameValid.value = true
                } else {
                    viewModel.checkUsernameExists().observe(viewLifecycleOwner, androidx.lifecycle.Observer { isUsernameFree ->
                        if (isUsernameFree) {
                            viewModel.isUsernameValid.value = true
                        } else {
                            viewModel.isUsernameValid.value = null
                        }
                    })
                }
            } else {
                viewModel.isUsernameValid.value = false
            }
            viewModel.changeIsValidParameters()
        })

        viewModel.userEnteredName.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            viewModel.isNameValid.value = Validations.isNameValid(it) || it.isEmpty()
            viewModel.changeIsValidParameters()
        })

        viewModel.userEnteredLastName.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            viewModel.isLastNameValid.value = Validations.isNameValid(it) || it.isEmpty()
            viewModel.changeIsValidParameters()
        })
    }
}