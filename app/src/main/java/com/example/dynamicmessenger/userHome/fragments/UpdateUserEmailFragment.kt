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
import com.example.dynamicmessenger.databinding.FragmentUpdateUserEmailBinding
import com.example.dynamicmessenger.userHome.viewModels.UpdateUserEmailViewModel
import com.example.dynamicmessenger.utils.Validations

class UpdateUserEmailFragment : Fragment() {

    private lateinit var viewModel: UpdateUserEmailViewModel
    private lateinit var binding: FragmentUpdateUserEmailBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(UpdateUserEmailViewModel::class.java)
        binding = FragmentUpdateUserEmailBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        //Toolbar
        setHasOptionsMenu(true)
        configureTopNavBar(binding.updateUserEmailToolbar)
        observers()
        onClickListeners()


        return binding.root
    }

    override fun onResume() {
        super.onResume()
        SharedConfigs.currentFragment.value = MyFragments.UPDATE_EMAIL
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
                viewModel.updateUserEmail(requireContext(), binding)
            } else {
                viewModel.verifyUserEmail(requireContext()) {closure ->
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
        viewModel.userEnteredEmail.observe(viewLifecycleOwner, Observer {
            viewModel.isEmailValid.value = Validations.isEmailValid(it)
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