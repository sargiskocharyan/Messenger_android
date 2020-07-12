package com.example.dynamicmessenger.authorization.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.dynamicmessenger.R
import com.example.dynamicmessenger.activitys.HomeActivity
import com.example.dynamicmessenger.authorization.viewModels.FinishRegistrationViewModel
import com.example.dynamicmessenger.databinding.FragmentFinishRegistrationBinding

class FinishRegistrationFragment : Fragment() {

    private lateinit var viewModel: FinishRegistrationViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
        val binding: FragmentFinishRegistrationBinding =
            FragmentFinishRegistrationBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this).get(FinishRegistrationViewModel::class.java)

        binding.nextButton.setOnClickListener {
            val intent = Intent(activity, HomeActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            (activity as Activity?)!!.overridePendingTransition(1, 1)
        }
        return binding.root
    }

}
