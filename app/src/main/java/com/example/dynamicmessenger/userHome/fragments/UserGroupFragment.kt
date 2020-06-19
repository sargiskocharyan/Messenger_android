package com.example.dynamicmessenger.userHome.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.dynamicmessenger.R
import com.example.dynamicmessenger.databinding.FragmentUserGroupBinding
import com.example.dynamicmessenger.userHome.viewModels.UserGroupViewModel


class UserGroupFragment : Fragment() {
    lateinit var viewModel: UserGroupViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding : FragmentUserGroupBinding =
            DataBindingUtil.inflate(inflater,
                R.layout.fragment_user_group,
                container,false)
        viewModel = ViewModelProvider(this).get(UserGroupViewModel::class.java)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        return binding.root
    }

}
