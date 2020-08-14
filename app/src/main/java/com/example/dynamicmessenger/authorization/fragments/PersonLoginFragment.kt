package com.example.dynamicmessenger.authorization.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.dynamicmessenger.activitys.HomeActivity
import com.example.dynamicmessenger.activitys.viewModels.MainActivityViewModel
import com.example.dynamicmessenger.authorization.viewModels.PersonLoginViewModel
import com.example.dynamicmessenger.databinding.FragmentPersonLoginBinding

class PersonLoginFragment : Fragment() {

    private lateinit var viewModel: PersonLoginViewModel
    private lateinit var binding: FragmentPersonLoginBinding
    private val activityViewModel: MainActivityViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
        binding = FragmentPersonLoginBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this).get(PersonLoginViewModel::class.java)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        configureViewModel()
        observers()

        binding.root.setOnClickListener {
            val view = requireActivity().currentFocus
            view?.let {
                val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                imm?.hideSoftInputFromWindow(it.windowToken, 0)
            }
        }

        return binding.root
    }

    private fun configureViewModel() {
        viewModel.personEmail.value = activityViewModel.userMail ?: ""
        viewModel.isEmailExist.value = activityViewModel.userMailExists ?: false
        viewModel.userEnteredCode.value = activityViewModel.userCode
    }

    private fun observers() {
        viewModel.userEnteredCode.observe(viewLifecycleOwner, Observer {
            viewModel.hintVisibility.value = it.isNotEmpty()
            viewModel.isCodeValid.value = it.length == 4
            activityViewModel.userCode = it
        })

        viewModel.goToNextPage.observe(viewLifecycleOwner, Observer {
            if (it) {
                val intent = Intent(requireActivity(), HomeActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
                (activity as Activity?)!!.overridePendingTransition(1, 1)
            }
        })

        viewModel.personEmail.observe(viewLifecycleOwner, Observer {
            activityViewModel.userMail = it
        })

        viewModel.isEmailExists.observe(viewLifecycleOwner, Observer  {
            activityViewModel.userMailExists = it
        })
    }
}
