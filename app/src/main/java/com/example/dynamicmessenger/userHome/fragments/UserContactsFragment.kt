package com.example.dynamicmessenger.userHome.fragments

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dynamicmessenger.R
import com.example.dynamicmessenger.activitys.HomeActivity
import com.example.dynamicmessenger.common.MyFragments
import com.example.dynamicmessenger.common.SharedConfigs
import com.example.dynamicmessenger.databinding.FragmentUserContactsBinding
import com.example.dynamicmessenger.dialogs.ContactsSearchDialog
import com.example.dynamicmessenger.network.authorization.models.User
import com.example.dynamicmessenger.userHome.adapters.UserContactsAdapter
import com.example.dynamicmessenger.userHome.viewModels.UserContactsViewModel


class UserContactsFragment : Fragment() {
    private lateinit var viewModel: UserContactsViewModel
    private lateinit var binding: FragmentUserContactsBinding
    private lateinit var adapter: UserContactsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(UserContactsViewModel::class.java)
        adapter = UserContactsAdapter(requireContext())
        binding = FragmentUserContactsBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.root.setHasTransientState(true)
        binding.contactsRecyclerView.adapter = adapter
        binding.contactsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        updateRecycleViewFromNetwork(adapter)

        SharedConfigs.currentFragment.value = MyFragments.CONTACTS
        HomeActivity.isAddContacts = false
//        viewModel.getSavedContacts().observe(viewLifecycleOwner, Observer {
//            updateRecycleView(adapter, it)
//        })
        viewModel.searchResult.observe(viewLifecycleOwner, Observer {
            viewModel.getSearchedContacts(it) {name ->
                updateRecycleView(adapter, name)
            }
        })

        //Toolbar
        setHasOptionsMenu(true)
        configureTopNavBar(binding.addUserContactsToolbar)

        return binding.root
    }

    //for show toolbar menu
    override fun onPrepareOptionsMenu(menu: Menu) {
        val inflater: MenuInflater = requireActivity().menuInflater
        inflater.inflate(R.menu.add_contacts_top_bar, menu)
        super.onPrepareOptionsMenu(menu)
    }

    private fun updateRecycleViewFromNetwork(adapter: UserContactsAdapter) {
        SharedConfigs.userRepository.getUserContacts().observe(viewLifecycleOwner, Observer {
            if (it != null) {
                adapter.setAdapterDataNotify(it)
                viewModel.saveContacts(it)
            }
        })
//        viewModel.getUserContactsFromNetwork(requireContext()) {
//            adapter.setAdapterDataNotify(it)
//        }
    }

    private fun updateRecycleView(adapter: UserContactsAdapter, data: List<User>) {
        scrollToTop()
        adapter.submitList(data)
    }

    private fun configureTopNavBar(toolbar: Toolbar) {
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
        toolbar.setOnMenuItemClickListener {
            val contactSearchDialog = ContactsSearchDialog(viewModel.searchResult)
            contactSearchDialog.show(requireActivity().supportFragmentManager, "Dialog")

            return@setOnMenuItemClickListener true
        }
    }

    private fun scrollToTop() {
        binding.contactsRecyclerView.scrollToPosition(0)
    }
}