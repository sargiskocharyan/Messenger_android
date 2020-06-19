package com.example.dynamicmessenger.userHome.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dynamicmessenger.R
import com.example.dynamicmessenger.databinding.FragmentUserContactsBinding
import com.example.dynamicmessenger.dialogs.ContactsSearchDialog
import com.example.dynamicmessenger.userHome.adapters.UserContactsAdapter
import com.example.dynamicmessenger.userHome.viewModels.UserContactsViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class UserContactsFragment : Fragment() {
    lateinit var viewModel: UserContactsViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding :FragmentUserContactsBinding =
            DataBindingUtil.inflate(inflater,
                R.layout.fragment_user_contacts,
                container,false)
        viewModel = ViewModelProvider(this).get(UserContactsViewModel::class.java)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        val adapter = UserContactsAdapter(requireContext(), viewModel)
        updateRecycleView(adapter)

        binding.addImageView.setOnClickListener {
            val exampleDialog = ContactsSearchDialog {myList ->
                adapter.data = myList
                adapter.notifyDataSetChanged()
            }
            exampleDialog.show(requireActivity().supportFragmentManager, "Dialog")
        }

        binding.root.setHasTransientState(true)
        binding.contactsRecyclerView.adapter = adapter
        val linearLayoutManager = LinearLayoutManager(requireContext())
        binding.contactsRecyclerView.layoutManager = linearLayoutManager

        binding.backImageView.setOnClickListener {
            val selectedFragment = UserInformationFragment()
            activity?.supportFragmentManager?.beginTransaction()?.replace(
                R.id.fragmentContainer,
                selectedFragment
            )?.commit()
        }
        return binding.root
    }

    private fun updateRecycleView(adapter: UserContactsAdapter) {
        viewModel.getUserContactsFromNetwork(requireContext()) {
            adapter.data = it
            adapter.notifyDataSetChanged()
        }
    }
}