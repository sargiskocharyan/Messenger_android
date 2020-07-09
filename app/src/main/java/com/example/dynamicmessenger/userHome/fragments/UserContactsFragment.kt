package com.example.dynamicmessenger.userHome.fragments

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dynamicmessenger.R
import com.example.dynamicmessenger.databinding.FragmentUserContactsBinding
import com.example.dynamicmessenger.dialogs.ContactsSearchDialog
import com.example.dynamicmessenger.network.authorization.models.UserContacts
import com.example.dynamicmessenger.userDataController.SharedPreferencesManager
import com.example.dynamicmessenger.userHome.adapters.UserContactsAdapter
import com.example.dynamicmessenger.userHome.adapters.UserContactsDiffUtilCallback
import com.example.dynamicmessenger.userHome.viewModels.UserContactsViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job


class UserContactsFragment : Fragment() {
    lateinit var viewModel: UserContactsViewModel
    private var fragmentJob = Job()
    private val coroutineScope = CoroutineScope(fragmentJob + Dispatchers.Main)

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
        updateRecycleViewFromNetwork(adapter)
        binding.root.setHasTransientState(true)
        binding.contactsRecyclerView.adapter = adapter
        val linearLayoutManager = LinearLayoutManager(requireContext())
        binding.contactsRecyclerView.layoutManager = linearLayoutManager
        SharedPreferencesManager.isAddContacts(requireContext(), false)
        //Toolbar
        setHasOptionsMenu(true)
        val toolbar: Toolbar = binding.addUserContactsToolbar
        configureTopNavBar(toolbar, adapter)

        return binding.root
    }

    //for show toolbar menu
    override fun onPrepareOptionsMenu(menu: Menu) {
        val inflater: MenuInflater = requireActivity().menuInflater
        inflater.inflate(R.menu.add_contacts_top_bar, menu)
        super.onPrepareOptionsMenu(menu)
    }

    override fun onDestroy() {
        super.onDestroy()
        fragmentJob.cancel()
    }

    private fun updateRecycleViewFromNetwork(adapter: UserContactsAdapter) {
        viewModel.getUserContactsFromNetwork(requireContext()) {
            updateRecycleView(adapter, it)
        }
    }

    private fun updateRecycleView(adapter: UserContactsAdapter, data: List<UserContacts>) {
        val userChatDiffUtilCallback = UserContactsDiffUtilCallback(adapter.data, data)
        val authorDiffResult = DiffUtil.calculateDiff(userChatDiffUtilCallback)
        adapter.data = data
        authorDiffResult.dispatchUpdatesTo(adapter)
    }

    private fun configureTopNavBar(toolbar: Toolbar, adapter: UserContactsAdapter) {
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
//        toolbar.title = title
        toolbar.elevation = 10.0F
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24)
//        toolbar.inflateMenu(R.menu.chat_top_bar)
        toolbar.background = ColorDrawable(ContextCompat.getColor(requireContext(), R.color.white))
        toolbar.setNavigationOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
        toolbar.setOnMenuItemClickListener {
            SharedPreferencesManager.isAddContacts(requireContext(), true)
            val contactSearchDialog = ContactsSearchDialog(coroutineScope) {myList ->
                updateRecycleView(adapter, myList)
            }
            contactSearchDialog.show(requireActivity().supportFragmentManager, "Dialog")

            return@setOnMenuItemClickListener true
        }
    }
}