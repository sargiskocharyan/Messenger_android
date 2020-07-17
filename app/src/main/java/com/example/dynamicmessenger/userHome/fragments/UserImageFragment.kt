package com.example.dynamicmessenger.userHome.fragments

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.dynamicmessenger.R
import com.example.dynamicmessenger.common.SharedConfigs
import com.example.dynamicmessenger.databinding.FragmentUserImageBinding
import com.example.dynamicmessenger.network.LoadAvatarApi
import com.example.dynamicmessenger.userDataController.database.DiskCache
import com.example.dynamicmessenger.userHome.viewModels.UserImageViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class UserImageFragment : Fragment() {
    private lateinit var viewModel: UserImageViewModel
    private lateinit var binding: FragmentUserImageBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(UserImageViewModel::class.java)
        binding = FragmentUserImageBinding.inflate(layoutInflater)
        val bottomNavBar: BottomNavigationView = requireActivity().findViewById(R.id.bottomNavigationView)
        bottomNavBar.visibility = View.GONE

        viewModel.getAvatar {
            binding.bigAvatarImageView.setImageBitmap(it)
        }

        binding.deleteAvatarImageView.setOnClickListener {
            viewModel.deleteUserAvatar {
                if (it) {
                    requireActivity().supportFragmentManager.popBackStack()
                } else {

                }
            }
        }

        binding.bigAvatarBackImageView.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        return binding.root
    }

}