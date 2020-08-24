package com.example.dynamicmessenger.userChatRoom.fragments

import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dynamicmessenger.R
import com.example.dynamicmessenger.activitys.HomeActivity
import com.example.dynamicmessenger.common.MyFragments
import com.example.dynamicmessenger.common.SharedConfigs
import com.example.dynamicmessenger.databinding.FragmentChatRoomBinding
import com.example.dynamicmessenger.network.authorization.models.ChatRoom
import com.example.dynamicmessenger.network.chatRooms.SocketManager
import com.example.dynamicmessenger.userChatRoom.adapters.ChatRoomAdapter
import com.example.dynamicmessenger.userChatRoom.viewModels.ChatRoomViewModel


class ChatRoomFragment : Fragment() {
    private lateinit var viewModel: ChatRoomViewModel
    private lateinit var binding: FragmentChatRoomBinding
    private lateinit var adapter: ChatRoomAdapter
    private var scrollUpWhenKeyboardOpened = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val myID = SharedConfigs.signedUser!!._id
        val receiverID = HomeActivity.receiverID!!
        val linearLayoutManager = LinearLayoutManager(requireContext())
        viewModel = ViewModelProvider(this).get(ChatRoomViewModel::class.java)
        adapter = ChatRoomAdapter(requireContext(), myID)
        binding = FragmentChatRoomBinding.inflate(layoutInflater)
        binding.chatRecyclerView.adapter = adapter
        binding.chatRecyclerView.layoutManager = linearLayoutManager
        binding.root.setHasTransientState(true)

        SharedConfigs.currentFragment.value = MyFragments.CHAT_ROOM
        //Toolbar
        setHasOptionsMenu(true)
        configureTopNavBar(binding.chatRoomToolbar)
        observers(receiverID, linearLayoutManager)
        updateRecyclerView(receiverID)

        //socket
        SocketManager.addChatRoomFragment(this)

        binding.sendMessageButton.setOnClickListener {
            SocketManager.sendMessage(receiverID, binding.sendMessageEditText)
        }

        return binding.root
    }

    //for show toolbar menu
    override fun onPrepareOptionsMenu(menu: Menu) {
        val inflater: MenuInflater = requireActivity().menuInflater
        inflater.inflate(R.menu.chat_top_bar, menu)
        super.onPrepareOptionsMenu(menu)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        SocketManager.removeChatRoomFragment()
//        HomeActivity.isAddContacts = false
    }

    private fun observers(receiverID: String, linearLayoutManager: LinearLayoutManager) {
        SharedConfigs.userRepository.getUserInformation(receiverID).observe(viewLifecycleOwner, Observer {user ->
            if (user != null) {
                HomeActivity.opponentUser = user
                binding.userChatToolbarTitle.text = user.username ?: ""
                SharedConfigs.userRepository.getAvatar(user.avatarURL).observe(viewLifecycleOwner, Observer {bitmap ->
                    adapter.receiverImage = bitmap
                })
            }
        })

        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                scrollToBottom()
            }
        })

        binding.chatRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                scrollUpWhenKeyboardOpened = linearLayoutManager.findLastVisibleItemPosition() > adapter.itemCount - 3
            }
        })

        binding.root.viewTreeObserver.addOnGlobalLayoutListener {
            val r = Rect()
            binding.root.getWindowVisibleDisplayFrame(r)
            val screenHeight = binding.root.rootView.height
            val keypadHeight: Int = screenHeight - r.bottom
            val isVisible = keypadHeight > screenHeight * 0.15
            if (viewModel.isKeyboardVisible.value != isVisible) {
                viewModel.isKeyboardVisible.value = isVisible
            }
        }

        viewModel.isKeyboardVisible.observe(viewLifecycleOwner, Observer {
            if (it && scrollUpWhenKeyboardOpened) {
                scrollToBottom()
            }
        })
    }

    private fun updateRecyclerView(receiverID: String) {
        viewModel.getMessagesFromNetwork(requireContext(), receiverID) {
            adapter.submitList(it)
            scrollToBottom()
        }
    }

    private fun scrollToBottom() {
        binding.chatRecyclerView.scrollToPosition(adapter.itemCount - 1)
    }

    private fun configureTopNavBar(toolbar: Toolbar) {
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
        toolbar.setOnMenuItemClickListener {
            activity?.supportFragmentManager
                ?.beginTransaction()
                ?.replace(R.id.fragmentContainer, OpponentInformationFragment())
                ?.addToBackStack(null)
                ?.commit()
            return@setOnMenuItemClickListener true
        }
    }

    fun receiveMessage(newMessage: ChatRoom) {
        if (HomeActivity.receiverID!! == newMessage.senderId || HomeActivity.receiverID!! == newMessage.reciever) {
            val newData = adapter.data.toMutableList()
            newData += newMessage
            adapter.submitList(newData)
            if (scrollUpWhenKeyboardOpened) {
                scrollToBottom()
            }
        }
    }
}