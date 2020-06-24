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
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.dynamicmessenger.R
import com.example.dynamicmessenger.activitys.MainActivity
import com.example.dynamicmessenger.customViews.spinner.CountryAdapter
import com.example.dynamicmessenger.customViews.spinner.CountryItem
import com.example.dynamicmessenger.databinding.FragmentUserInformationBinding
import com.example.dynamicmessenger.network.chatRooms.SocketManager
import com.example.dynamicmessenger.userDataController.SaveToken
import com.example.dynamicmessenger.userDataController.SharedPreferencesManager
import com.example.dynamicmessenger.userHome.viewModels.UserInformationViewModel
import com.github.nkzawa.socketio.client.Socket


class UserInformationFragment : Fragment() {
    private lateinit var viewModel: UserInformationViewModel
    private var mCountryList: ArrayList<CountryItem> = ArrayList()

    @SuppressLint("ResourceType")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding : FragmentUserInformationBinding =
            DataBindingUtil.inflate(inflater,
                R.layout.fragment_user_information,
                container, false)
        viewModel = ViewModelProvider(this).get(UserInformationViewModel::class.java)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        val userObject = SharedPreferencesManager.loadUserObject(requireContext())
        binding.username.text = userObject?.username ?: ""
        binding.email.text = userObject?.email ?: ""
        binding.name.text = userObject?.name ?: "Name"
        binding.lastName.text = userObject?.lastname ?: "Last Name"
        binding.university.text = userObject?.university?.name ?: "University"

        initList()
        val mAdapter = CountryAdapter(requireContext(), mCountryList)
        binding.languageSpinner.adapter = mAdapter

        binding.languageSpinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>?,
                selectedItemView: View?,
                position: Int,
                id: Long
            ) {
                selectedItemView?.findViewById<TextView>(R.id.textViewName)?.visibility = View.GONE
            }
            override fun onNothingSelected(parentView: AdapterView<*>?) {
            }
        }

        binding.contactsButton.setOnClickListener {
            val selectedFragment = UserContactsFragment()
            activity?.supportFragmentManager?.beginTransaction()?.replace(
                R.id.fragmentContainer,
                selectedFragment
            )?.commit()
        }

        binding.darkModeSwitch.isChecked = SharedPreferencesManager.getDarkMode(requireContext())

        binding.darkModeSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                SharedPreferencesManager.setDarkMode(requireContext(), true)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                SharedPreferencesManager.setDarkMode(requireContext(), false)
            }
        }

        binding.logoutButton.setOnClickListener {
            val token = SaveToken.decrypt(SharedPreferencesManager.getUserToken(requireContext()))
            viewModel.logoutNetwork(token, requireContext()) {
                if (it) {
                    //TODO UserDataManager.logoutUser()
                    SharedPreferencesManager.setUserToken(requireContext(), "")
                    SharedPreferencesManager.deleteUserAllInformation(requireContext())
                    val intent = Intent(activity, MainActivity::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                    (activity as Activity?)!!.overridePendingTransition(1, 1)
                }
            }
        }

        binding.updateUserInformationImageView.setOnClickListener {
            val selectedFragment = UpdateUserInformationFragment()
            activity?.supportFragmentManager?.beginTransaction()?.replace(
                R.id.fragmentContainer,
                selectedFragment
            )?.commit()
        }

        return binding.root
    }

    private fun initList() {
        mCountryList.add(CountryItem("English", R.drawable.ic_united_kingdom))
        mCountryList.add(CountryItem("Русский", R.drawable.ic_russia))
        mCountryList.add(CountryItem("Հայերեն", R.drawable.ic_armenia))
    }

}
