package com.example.dynamicmessenger.userHome.fragments

//import com.example.dynamicmessenger.activitys.App
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.dynamicmessenger.R
import com.example.dynamicmessenger.activitys.MainActivity
import com.example.dynamicmessenger.common.AppLangKeys
import com.example.dynamicmessenger.common.MyFragments
import com.example.dynamicmessenger.common.SharedConfigs
import com.example.dynamicmessenger.databinding.FragmentUserInformationBinding
import com.example.dynamicmessenger.network.chatRooms.SocketManager
import com.example.dynamicmessenger.userDataController.SharedPreferencesManager
import com.example.dynamicmessenger.userHome.viewModels.UserInformationViewModel
import com.example.dynamicmessenger.utils.LocalizationUtil
import com.example.dynamicmessenger.utils.ToByteArray
import com.google.android.material.bottomnavigation.BottomNavigationView
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.InputStream
import java.lang.Exception
import java.net.URL


class UserInformationFragment : Fragment() {
    private lateinit var viewModel: UserInformationViewModel
    private lateinit var binding: FragmentUserInformationBinding

    @SuppressLint("ResourceType")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserInformationBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this).get(UserInformationViewModel::class.java)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        val bottomNavBar: BottomNavigationView = requireActivity().findViewById(R.id.bottomNavigationView)
        bottomNavBar.visibility = View.VISIBLE
        SharedConfigs.currentFragment.value = MyFragments.INFORMATION
        changeDarkMode()
        observers()
        popupMenu(binding)
        onClickListeners()

        binding.darkModeSwitch.isChecked = SharedConfigs.getDarkMode()

        return binding.root
    }

    @SuppressLint("Recycle")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            val uri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, uri)
            binding.userProfileImageView.setImageBitmap(bitmap)

            val inputStream: InputStream = data.data?.let { requireActivity().contentResolver.openInputStream(it) }!!
            val requestFile = RequestBody.create(MediaType.parse("image/jpg"), ToByteArray.getBytes(inputStream)!!)
            val body = MultipartBody.Part.createFormData("avatar", "avatar.jpg", requestFile)
            viewModel.saveUserAvatarFromNetwork(requireContext(), body, binding)
        }
    }

    private fun observers() {
        SharedConfigs.appLang.observe(viewLifecycleOwner, Observer {
            viewModel.appLanguage.value = it
        })

        SharedConfigs.userRepository.getAvatar(SharedConfigs.signedUser?.avatarURL).observe(viewLifecycleOwner, Observer {
            viewModel.avatarBitmap.value = it
        })
    }

    private fun onClickListeners() {
        binding.languageConstraintLayout.setOnClickListener {
            binding.languagePopupMenuLinearLayout.visibility = View.VISIBLE
        }

        binding.contactConstraintLayout.setOnClickListener {
            SharedConfigs.lastFragment = MyFragments.INFORMATION
            navigateToFragment(UserContactsFragment())
        }

        binding.darkModeSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                SharedConfigs.setDarkMode(true)
//                LocalizationUtil.setApplicationLocale(requireContext(), SharedConfigs.appLang.value!!.value)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                SharedConfigs.setDarkMode(false)
//                LocalizationUtil.setApplicationLocale(requireContext(), SharedConfigs.appLang.value!!.value)
            }
//            SharedConfigs.currentFragment.value = MyFragments.INFORMATION
        }

        binding.logoutConstraintLayout.setOnClickListener {
            viewModel.logoutNetwork(SharedConfigs.token, requireContext()) {
                if (it) {
                    SharedPreferencesManager.deleteUserAllInformation(requireContext())
                    SharedConfigs.deleteToken()
                    SharedConfigs.deleteSignedUser()
                    SharedConfigs.userRepository.deleteAllData()
                    val intent = Intent(requireActivity(), MainActivity::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                    SocketManager.closeSocket()
                }
            }
        }

        binding.uploadUserImageImageView.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }

        binding.userProfileImageView.setOnClickListener {
            navigateToFragment(UserImageFragment())
        }

        binding.updateUserInformationImageView.setOnClickListener {
            navigateToFragment(UpdateUserInformationFragment())
        }

        binding.updateUserEmailImageView.setOnClickListener {
            navigateToFragment(UpdateUserEmailFragment())
        }

        binding.updateUserPhoneNumberImageView.setOnClickListener {
            navigateToFragment(UpdateUserPhoneNumberFragment())
        }

        binding.userInformationConstraintLayout.setOnClickListener {
            binding.languagePopupMenuLinearLayout.visibility = View.GONE
        }
        binding.userInformationScrollView.setOnClickListener {
            binding.languagePopupMenuLinearLayout.visibility = View.GONE
        }
    }


    private fun popupMenu(binding: FragmentUserInformationBinding) {
        binding.languageEn.setOnClickListener {
            SharedConfigs.changeAppLanguage(AppLangKeys.EN)
            LocalizationUtil.setApplicationLocale(requireContext(), SharedConfigs.appLang.value!!.value)
            requireActivity().supportFragmentManager.beginTransaction().detach(this).attach(this).commit()
        }

        binding.languageRu.setOnClickListener {
            SharedConfigs.changeAppLanguage(AppLangKeys.RU)
            LocalizationUtil.setApplicationLocale(requireContext(), SharedConfigs.appLang.value!!.value)
            requireActivity().supportFragmentManager.beginTransaction().detach(this).attach(this).commit()
        }

        binding.languageAm.setOnClickListener {
            SharedConfigs.changeAppLanguage(AppLangKeys.AM)
            LocalizationUtil.setApplicationLocale(requireContext(), SharedConfigs.appLang.value!!.value)
            requireActivity().supportFragmentManager.beginTransaction().detach(this).attach(this).commit()
        }
    }

    private fun changeDarkMode() {
        if (SharedConfigs.getDarkMode()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    private fun navigateToFragment(fragment: Fragment) {
        activity?.supportFragmentManager
            ?.beginTransaction()
            ?.replace(R.id.fragmentContainer, fragment)
            ?.addToBackStack(null)
            ?.commit()
    }
}
