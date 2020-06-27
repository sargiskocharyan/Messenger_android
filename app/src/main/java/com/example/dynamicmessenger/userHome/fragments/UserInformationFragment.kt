package com.example.dynamicmessenger.userHome.fragments

//import com.example.dynamicmessenger.activitys.App
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
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
import com.example.dynamicmessenger.userHome.viewModels.UserInformationViewModel
import com.example.dynamicmessenger.utils.LocalizationUtil
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import java.io.*


class UserInformationFragment : Fragment() {
    private lateinit var viewModel: UserInformationViewModel
    private lateinit var binding: FragmentUserInformationBinding
    private var mCountryList: ArrayList<CountryItem> = ArrayList()

    @RequiresApi(Build.VERSION_CODES.Q)
    @SuppressLint("ResourceType")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
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

        binding.contactConstraintLayout.setOnClickListener {
            val selectedFragment = UserContactsFragment()
            activity?.supportFragmentManager?.beginTransaction()?.replace(
                R.id.fragmentContainer,
                selectedFragment
            )?.commit()
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

        viewModel.getUserAvatarFromNetwork(requireContext(), SharedConfigs.signedUser!!._id) {
            val bmp = BitmapFactory.decodeStream(it.byteStream())
            binding.userProfileImageView.setImageBitmap(bmp)
        }


        binding.userProfileImageView.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
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

    @SuppressLint("Recycle")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            val uri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, uri)
            binding.userProfileImageView.setImageBitmap(bitmap)

            val inputStream: InputStream = data.data?.let { requireActivity().contentResolver.openInputStream(it) }!!
            /*
//            val f = File(requireContext().cacheDir, "avatar.png")
//            f.createNewFile()
//            val bos = ByteArrayOutputStream()
//            bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos)
//            val bitmapData = bos.toByteArray()
//            val fos = FileOutputStream(f)
//            fos.write(bitmapData)
//            fos.flush()
//            fos.close()
            */
            val requestFile = RequestBody.create(MediaType.parse("image/jpg"), getBytes(inputStream)!!)
            val body = MultipartBody.Part.createFormData("avatar", "avatar.jpg", requestFile)
            viewModel.saveUserAvatarFromNetwork(requireContext(), body)
        }
    }

    @Throws(IOException::class)
    fun getBytes(inputStream: InputStream): ByteArray? {
        val byteBuff = ByteArrayOutputStream()
        val buffSize = 1024
        val buff = ByteArray(buffSize)
        var len = 0
        while (inputStream.read(buff).also { len = it } != -1) {
            byteBuff.write(buff, 0, len)
        }
        return byteBuff.toByteArray()
    }

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
                    LocalizationUtil.setApplicationLocale(requireContext(), SharedConfigs.appLang.value)
                    requireFragmentManager().beginTransaction().detach(this).attach(this).commit()
                }
                R.id.languageRu -> {
                    SharedConfigs.appLang = AppLangKeys.RU
                    binding.languageImage.setImageResource(R.drawable.ic_russia)
                    LocalizationUtil.setApplicationLocale(requireContext(), SharedConfigs.appLang.value)
                    requireFragmentManager().beginTransaction().detach(this).attach(this).commit()
                }
                else -> {
                    SharedConfigs.appLang = AppLangKeys.AM
                    binding.languageImage.setImageResource(R.drawable.ic_armenia)
                    LocalizationUtil.setApplicationLocale(requireContext(), SharedConfigs.appLang.value)
                    requireFragmentManager().beginTransaction().detach(this).attach(this).commit()
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
}
