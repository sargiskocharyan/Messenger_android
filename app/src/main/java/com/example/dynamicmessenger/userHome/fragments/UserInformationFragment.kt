package com.example.dynamicmessenger.userHome.fragments

//import com.example.dynamicmessenger.userDataController.App
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.dynamicmessenger.R
import com.example.dynamicmessenger.activitys.MainActivity
import com.example.dynamicmessenger.common.AppLangKeys
import com.example.dynamicmessenger.common.SharedConfigs
import com.example.dynamicmessenger.customViews.spinner.CountryAdapter
import com.example.dynamicmessenger.customViews.spinner.CountryItem
import com.example.dynamicmessenger.databinding.FragmentUserInformationBinding
import com.example.dynamicmessenger.userDataController.SaveToken
import com.example.dynamicmessenger.userDataController.SharedPreferencesManager
import com.example.dynamicmessenger.userDataController.UserDataManager
import com.example.dynamicmessenger.userHome.viewModels.UserInformationViewModel
import com.example.dynamicmessenger.utils.LocalizationUtil
import java.util.*
import kotlin.collections.ArrayList


class UserInformationFragment : Fragment() {
    private lateinit var viewModel: UserInformationViewModel
    private var mCountryList: ArrayList<CountryItem> = ArrayList()

    @RequiresApi(Build.VERSION_CODES.Q)
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

        val userObject = SharedConfigs.signedUser
        binding.username.text = userObject?.username ?: ""
        binding.email.text = userObject?.email ?: ""
        binding.name.text = userObject?.name ?: "Name"
        binding.lastName.text = userObject?.lastname ?: "Last Name"
        binding.university.text = userObject?.university?.name ?: "University"

        val mAdapter = CountryAdapter(requireContext(), mCountryList)

        popupMenu(binding)
        setLanguageImage(binding)

        setLocale(requireContext(), SharedConfigs.appLang.value)

//        override fun attachBaseContext(base: Context?) {
//            super.attachBaseContext(LocalizationUtil.applyLanguage(base!!, "ru"))
//        }
//        val languageToLoad = SharedConfigs.appLang.value
//
//        val locale = Locale(languageToLoad)
//        Locale.setDefault(locale)
//        val config = Configuration()
//        config.locale = locale
//        requireContext().resources.updateConfiguration(
//            config,
//            requireContext().resources.displayMetrics
//        )

        binding.contactConstraintLayout.setOnClickListener {
            val selectedFragment = UserContactsFragment()
            activity?.supportFragmentManager?.beginTransaction()?.replace(
                R.id.fragmentContainer,
                selectedFragment
            )?.commit()
        }

        binding.darkModeSwitch.isChecked = UserDataManager.getDarkMode()

        binding.darkModeSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                UserDataManager.setDarkMode(true)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                UserDataManager.setDarkMode(false)
            }
        }

        binding.logoutConstraintLayout.setOnClickListener {
            val token = SaveToken.decrypt(SharedPreferencesManager.getUserToken(requireContext()))
            viewModel.logoutNetwork(token!!, requireContext()) {
                if (it) {
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

//    override fun onAttach(context: Context) {
//        super.onAttach(LocalizationUtil.applyLanguage(context, SharedConfigs.appLang.value))
//    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun popupMenu(binding: FragmentUserInformationBinding) {
        val popup = PopupMenu(requireContext(), binding.languageConstraintLayout)
        popup.inflate(R.menu.popup_language_menu)
        popup.setForceShowIcon(true)
        popup.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.languageEn -> {
                    SharedConfigs.appLang = AppLangKeys.EN
                    binding.languageImage.setImageResource(R.drawable.ic_united_kingdom)
//                    LocalizationUtil.applyLanguage(requireContext(), SharedConfigs.appLang.value)setApplicationLocale
//                    LocalizationUtil.setApplicationLocale(requireContext(), SharedConfigs.appLang.value)
                    retainInstance = true
                    Log.i("+++","${SharedConfigs.appLang}")
                }
                R.id.languageRu -> {
                    SharedConfigs.appLang = AppLangKeys.RU
                    binding.languageImage.setImageResource(R.drawable.ic_russia)
//                    LocalizationUtil.applyLanguage(requireContext(), SharedConfigs.appLang.value)
//                    LocalizationUtil.setApplicationLocale(requireContext(), SharedConfigs.appLang.value)
//                    setLocale(requireContext(), SharedConfigs.appLang.value)
                    retainInstance = true
                    Log.i("+++","${SharedConfigs.appLang}")
                }
                else -> {
                    SharedConfigs.appLang = AppLangKeys.AM
                    binding.languageImage.setImageResource(R.drawable.ic_armenia)
//                    LocalizationUtil.applyLanguage(requireContext(), SharedConfigs.appLang.value)
//                    LocalizationUtil.setApplicationLocale(requireContext(), SharedConfigs.appLang.value)
//                    setLocale(requireContext(), SharedConfigs.appLang.value)
                    retainInstance = true
                    Log.i("+++","${SharedConfigs.appLang}")
                }
            }
            true
        }
        binding.languageConstraintLayout.setOnClickListener {
            popup.show()
        }
    }

    private fun setLanguageImage(binding: FragmentUserInformationBinding) {
        when (SharedConfigs.appLang) {
            AppLangKeys.EN -> {
                binding.languageImage.setImageResource(R.drawable.ic_united_kingdom)
            }
            AppLangKeys.RU -> {
                binding.languageImage.setImageResource(R.drawable.ic_russia)
            }
            else -> {
                binding.languageImage.setImageResource(R.drawable.ic_armenia)
            }
        }
    }

    private fun setLocale(context: Context, lang: String) {
        val locale = Locale(lang)
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        context.resources.updateConfiguration(config, context.resources.displayMetrics)

    }
}
