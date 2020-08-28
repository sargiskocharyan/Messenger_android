package com.example.dynamicmessenger.userHome.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.dynamicmessenger.R
import com.example.dynamicmessenger.common.MyFragments
import com.example.dynamicmessenger.common.SharedConfigs
import com.example.dynamicmessenger.databinding.FragmentUpdateUserPhoneNumberBinding
import com.example.dynamicmessenger.userHome.viewModels.UpdateUserPhoneNumberViewModel
import com.example.dynamicmessenger.utils.Validations

class UpdateUserPhoneNumberFragment : Fragment() {

    private lateinit var viewModel: UpdateUserPhoneNumberViewModel
    private lateinit var binding: FragmentUpdateUserPhoneNumberBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(UpdateUserPhoneNumberViewModel::class.java)
        binding = FragmentUpdateUserPhoneNumberBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        //Toolbar
        setHasOptionsMenu(true)
        val toolbar: Toolbar = binding.updateUserPhoneNumberToolbar
        configureTopNavBar(toolbar)
        observers()
        onClickListeners()

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        SharedConfigs.currentFragment.value = MyFragments.UPDATE_PHONE_NUMBER
    }

    private fun onClickListeners() {
        binding.root.setOnClickListener {
            val view = requireActivity().currentFocus
            view?.let {
                val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                imm?.hideSoftInputFromWindow(it.windowToken, 0)
            }
        }

        binding.continueButton.setOnClickListener {
            if (viewModel.userEnteredCode.value == null || viewModel.userEnteredCode.value == "") {
                viewModel.updateUserPhoneNumber(requireContext(), binding)
            } else {
                viewModel.verifyUserPhoneNumber(requireContext()) {closure ->
                    if (closure) {
                        val selectedFragment = UserInformationFragment()
                        activity?.supportFragmentManager
                            ?.beginTransaction()
                            ?.replace(R.id.fragmentContainer, selectedFragment)
                            ?.addToBackStack(null)
                            ?.commit()
                    }
                }
            }//david.melikyan.1999@mail.ruru
        }
    }

    private fun observers() {
        viewModel.userEnteredPhoneNumber.observe(viewLifecycleOwner, Observer {
            viewModel.isPhoneNumberValid.value = Validations.isPhoneNumberValid(it)
        })

        viewModel.userEnteredCode.observe(viewLifecycleOwner, Observer {
            viewModel.isCodeValid.value = it.length == 4
        })
    }

    private fun configureTopNavBar(toolbar: Toolbar) {
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

}