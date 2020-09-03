package com.example.dynamicmessenger.userHome.fragments

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dynamicmessenger.R
import com.example.dynamicmessenger.common.MyFragments
import com.example.dynamicmessenger.common.SharedConfigs
import com.example.dynamicmessenger.databinding.FragmentUserCallBinding
import com.example.dynamicmessenger.network.chatRooms.SocketManager
import com.example.dynamicmessenger.userDataController.database.SignedUserDatabase
import com.example.dynamicmessenger.userDataController.database.UserCalls
import com.example.dynamicmessenger.userDataController.database.UserCallsDao
//import com.example.dynamicmessenger.userDataController.database.UserCallsRepository
import com.example.dynamicmessenger.userHome.adapters.UserCallsAdapter
import com.example.dynamicmessenger.userHome.viewModels.UserCallViewModel
import com.example.dynamicmessenger.utils.ClassConverter
import com.example.dynamicmessenger.utils.observeOnce
import com.google.android.material.bottomnavigation.BottomNavigationView

class UserCallFragment : Fragment() {

    private lateinit var binding: FragmentUserCallBinding
    private lateinit var callsDao: UserCallsDao
//    private lateinit var callsRepository: UserCallsRepository
    private lateinit var adapter: UserCallsAdapter
    private lateinit var viewModel: UserCallViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(UserCallViewModel::class.java)
        binding = FragmentUserCallBinding.inflate(layoutInflater)
        callsDao = SignedUserDatabase.getUserDatabase(requireContext())!!.userCallsDao()
        adapter = UserCallsAdapter(requireContext(), viewModel)

        binding.root.setHasTransientState(true)
        binding.callRecyclerView.adapter = adapter
        binding.lifecycleOwner = this
        binding.callRecyclerView.layoutManager = LinearLayoutManager(requireContext())


        SharedConfigs.userRepository.getUserCalls().first.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                adapter.submitList(it.reversed())
                scrollToTop()
            }
        })

        SharedConfigs.signedUser?.missedCallHistory?.let {list ->
            if (!list.isNullOrEmpty()) {
                SharedConfigs.userRepository.getUserCalls().second.observeOnce(viewLifecycleOwner, Observer {
                    if (it == true) {
                        val bottomNavBar: BottomNavigationView = requireActivity().findViewById(R.id.bottomNavigationView)
                        val badge = bottomNavBar.getOrCreateBadge(R.id.call)
                        badge.isVisible = false
                        SharedConfigs.signedUser = SharedConfigs.signedUser?.apply {
                            missedCallHistory = null
                        }
                    }
                })
            }
        }


        val simpleCallback: ItemTouchHelper.SimpleCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, viewHolder1: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, i: Int) {
                val position = viewHolder.adapterPosition
                adapter.deleteItem(position)
            }
        }
        val itemTouchHelper = ItemTouchHelper(simpleCallback)
        itemTouchHelper.attachToRecyclerView(binding.callRecyclerView)

        //toolbar
        setHasOptionsMenu(true)
        configureTopNavBar(binding.userChatToolbar)
        SocketManager.addCallFragment(this)

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        SharedConfigs.currentFragment.value = MyFragments.CALLS
    }

    override fun onDestroy() {
        super.onDestroy()
        SocketManager.removeCallFragment()
    }

    //for show toolbar menu
    override fun onPrepareOptionsMenu(menu: Menu) {
        val inflater: MenuInflater = requireActivity().menuInflater
        inflater.inflate(R.menu.plus_top_bar, menu)
        super.onPrepareOptionsMenu(menu)
    }

    private fun configureTopNavBar(toolbar: Toolbar) {
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        toolbar.setOnMenuItemClickListener {
            val selectedFragment = UserContactsFragment()
            SharedConfigs.lastFragment = MyFragments.CALLS
            requireActivity().supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragmentContainer, selectedFragment)
                .addToBackStack(null)
                .commit()

            return@setOnMenuItemClickListener true
        }
    }

    private fun scrollToTop() {
        binding.callRecyclerView.scrollToPosition(0)
    }

    fun receivedNewCall(userCall: UserCalls) {
        requireActivity().runOnUiThread {
            adapter.receiverNewMessage(userCall)
            scrollToTop()
        }
    }
}
