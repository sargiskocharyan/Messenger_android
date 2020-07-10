package com.example.dynamicmessenger.userHome.fragments

//import com.example.dynamicmessenger.activitys.App
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.dynamicmessenger.R
import com.example.dynamicmessenger.activitys.MainActivity
import com.example.dynamicmessenger.common.AppLangKeys
import com.example.dynamicmessenger.common.SharedConfigs
import com.example.dynamicmessenger.databinding.FragmentUserInformationBinding
import com.example.dynamicmessenger.network.DownloadImageTask
import com.example.dynamicmessenger.userDataController.SharedPreferencesManager
import com.example.dynamicmessenger.userDataController.database.SignedUserDatabase
import com.example.dynamicmessenger.userHome.viewModels.UserInformationViewModel
import com.example.dynamicmessenger.utils.LocalizationUtil
import com.example.dynamicmessenger.utils.ToByteArray
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.InputStream


class UserInformationFragment : Fragment() {
    private lateinit var viewModel: UserInformationViewModel
    private lateinit var binding: FragmentUserInformationBinding
    private var activityJob = Job()
    private val coroutineScope = CoroutineScope(activityJob + Dispatchers.Main)

    @SuppressLint("ResourceType")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_user_information,
                container, false
            )
        val chatDao = SignedUserDatabase.getUserDatabase(requireContext())!!.userChatDao()
        viewModel = ViewModelProvider(this).get(UserInformationViewModel::class.java)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        binding.university.text = SharedConfigs.signedUser?.university?.toString() ?: "University"
        val bottomNavBar: BottomNavigationView =
            requireActivity().findViewById(R.id.bottomNavigationView)
        bottomNavBar.visibility = View.VISIBLE
        changeDarkMode()
//        popupMenu(binding)

        binding.languageConstraintLayout.setOnClickListener {
            binding.languagePopupMenuLinearLayout.visibility = View.VISIBLE
            binding.darkModeConstraintLayout.visibility = View.INVISIBLE
            binding.logoutConstraintLayout.visibility = View.INVISIBLE
        }
        popupMenu(binding)

//        setLanguageImage(binding)

        binding.contactConstraintLayout.setOnClickListener {
            val selectedFragment = UserContactsFragment()
            activity?.supportFragmentManager
                ?.beginTransaction()
                ?.replace(R.id.fragmentContainer, selectedFragment)
                ?.addToBackStack(null)
                ?.commit()
        }

        binding.darkModeSwitch.isChecked = SharedConfigs.getDarkMode()

        binding.darkModeSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                SharedConfigs.setDarkMode(true)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                SharedConfigs.setDarkMode(false)
            }
        }

        binding.logoutConstraintLayout.setOnClickListener {
            viewModel.logoutNetwork(SharedConfigs.token, requireContext()) {
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

//        SharedConfigs.signedUser?.avatarURL?.let { imageLoader.display(it, binding.userProfileImageView, R.drawable.ic_user_image) }
        viewModel.getAvatar {
            binding.userProfileImageView.setImageBitmap(it)
        }

        binding.uploadUserImageImageView.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }

        binding.userProfileImageView.setOnClickListener {
            val selectedFragment = UserImageFragment()
            activity?.supportFragmentManager
                ?.beginTransaction()
                ?.replace(R.id.fragmentContainer, selectedFragment)
                ?.addToBackStack(null)
                ?.commit()
        }

        binding.updateUserInformationImageView.setOnClickListener {
            val selectedFragment = UpdateUserInformationFragment()
            activity?.supportFragmentManager
                ?.beginTransaction()
                ?.replace(R.id.fragmentContainer, selectedFragment)
                ?.addToBackStack(null)
                ?.commit()
        }
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        activityJob.cancel()
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


    private fun popupMenu(binding: FragmentUserInformationBinding) {
        binding.languageEn.setOnClickListener {
            SharedConfigs.appLang = AppLangKeys.EN
            binding.languageImage.setImageResource(R.drawable.ic_united_kingdom)
            LocalizationUtil.setApplicationLocale(requireContext(), SharedConfigs.appLang.value)
            requireActivity().supportFragmentManager.beginTransaction().detach(this).attach(this).commit()
        }

        binding.languageRu.setOnClickListener {
            SharedConfigs.appLang = AppLangKeys.RU
            binding.languageImage.setImageResource(R.drawable.ic_russia)
            LocalizationUtil.setApplicationLocale(requireContext(), SharedConfigs.appLang.value)
            requireActivity().supportFragmentManager.beginTransaction().detach(this).attach(this).commit()
        }

        binding.languageAm.setOnClickListener {
            SharedConfigs.appLang = AppLangKeys.AM
            binding.languageImage.setImageResource(R.drawable.ic_armenia)
            LocalizationUtil.setApplicationLocale(requireContext(), SharedConfigs.appLang.value)
            requireActivity().supportFragmentManager.beginTransaction().detach(this).attach(this).commit()
        }
    }

    private fun setAvatar() {
        if (SharedConfigs.signedUser?.avatarURL != null) {
            DownloadImageTask(binding.userProfileImageView).execute(SharedConfigs.signedUser!!.avatarURL)
        }
    }

    private fun changeDarkMode() {
        if (SharedConfigs.getDarkMode()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }
}
