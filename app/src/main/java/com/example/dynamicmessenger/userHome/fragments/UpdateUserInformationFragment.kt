package com.example.dynamicmessenger.userHome.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.dynamicmessenger.R
import com.example.dynamicmessenger.activitys.MainActivity
import com.example.dynamicmessenger.common.SharedConfigs
import com.example.dynamicmessenger.databinding.FragmentUpdateUserInformationBinding
import com.example.dynamicmessenger.dialogs.DeactivateUserDialog
import com.example.dynamicmessenger.dialogs.DeleteUserDialog
import com.example.dynamicmessenger.network.authorization.models.UniversityProperty
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUpdateUserInformationBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this).get(UpdateUserInformationViewModel::class.java)
        binding.lifecycleOwner = this
        binding.updateUserViewModel = viewModel
        viewModel.userEnteredUsername.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            @SuppressLint("ResourceAsColor")
                if (Validations.isUsernameValid(binding.usernameEditText.text.toString())
                    && Validations.isNameValid(binding.nameEditText.text.toString())
                    && Validations.isLastNameValid(binding.lastNameEditText.text.toString())) {
                    binding.continueButton.isEnabled = true
                    binding.continueButton.setBackgroundResource(R.drawable.enable_button_design)
                } else {
                    binding.continueButton.isEnabled = false
                    binding.continueButton.setBackgroundResource(R.drawable.disable_button_design)
                }
        })

        viewModel.userEnteredName.value = SharedConfigs.signedUser?.name ?: ""
        viewModel.userEnteredLastName.value = SharedConfigs.signedUser?.lastname ?: ""
        viewModel.userEnteredUsername.value = SharedConfigs.signedUser?.username ?: ""

        //Bottom bar
        val bottomNavBar: BottomNavigationView = requireActivity().findViewById(R.id.bottomNavigationView)
        bottomNavBar.visibility = View.GONE
        datePicker = DatePickerHelper(requireContext(), true)

        //Toolbar
        setHasOptionsMenu(true)
        val toolbar: Toolbar = binding.updateUserInformationToolbar
        configureTopNavBar(toolbar)

        var university: String = ""
        var allUniversity: List<UniversityProperty>
        viewModel.getAllUniversity(requireContext()){
            allUniversity = it
            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                allUniversity
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.universitySpinner.adapter = adapter
            binding.universitySpinner.onItemSelectedListener = object :
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

        binding.birthDateEditText.setOnClickListener {
            showDatePickerDialog()
        }

        binding.continueButton.setOnClickListener {
            val birthDate = binding.birthDateEditText.text.toString()
            val name = viewModel.userEnteredName.value
            val lastName = viewModel.userEnteredLastName.value
            val username = viewModel.userEnteredUsername.value
            val phoneNumber = viewModel.userEnteredPhoneNumber.value
            val updateUserTask = UpdateUserTask(name, lastName, username, university, phoneNumber, birthday = birthDate)
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
                binding.birthDateEditText.text = "$monthStr/$dayStr/$year"
            }
        })
    }

    private fun configureTopNavBar(toolbar: Toolbar) {
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        toolbar.elevation = 10.0F
        toolbar.setNavigationOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }
}