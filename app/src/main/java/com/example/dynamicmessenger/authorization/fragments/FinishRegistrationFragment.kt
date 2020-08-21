package com.example.dynamicmessenger.authorization.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import com.example.dynamicmessenger.activitys.HomeActivity
import com.example.dynamicmessenger.authorization.viewModels.FinishRegistrationViewModel
import com.example.dynamicmessenger.databinding.FragmentFinishRegistrationBinding

class FinishRegistrationFragment : Fragment() {

    private lateinit var binding: FragmentFinishRegistrationBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFinishRegistrationBinding.inflate(layoutInflater)

        binding.nextButton.setOnClickListener {
            val intent = Intent(activity, HomeActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }

        binding.root.setOnClickListener {
            val view = requireActivity().currentFocus
            view?.let { v ->
                val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                imm?.hideSoftInputFromWindow(v.windowToken, 0)
            }
        }

        return binding.root
    }

}
