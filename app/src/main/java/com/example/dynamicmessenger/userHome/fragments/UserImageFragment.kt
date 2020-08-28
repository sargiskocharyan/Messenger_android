package com.example.dynamicmessenger.userHome.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.dynamicmessenger.common.MyFragments
import com.example.dynamicmessenger.common.SharedConfigs
import com.example.dynamicmessenger.databinding.FragmentUserImageBinding
import com.example.dynamicmessenger.userHome.viewModels.UserImageViewModel


class UserImageFragment : Fragment() {
    private lateinit var viewModel: UserImageViewModel
    private lateinit var binding: FragmentUserImageBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(UserImageViewModel::class.java)
        binding = FragmentUserImageBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        viewModel.getAvatar()

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

    override fun onResume() {
        super.onResume()
        SharedConfigs.currentFragment.value = MyFragments.USER_IMAGE
    }

}