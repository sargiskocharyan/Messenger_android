package com.example.dynamicmessenger.userChatRoom.fragments

import android.graphics.Rect
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.*
import android.widget.Toast
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
import com.example.dynamicmessenger.network.authorization.models.ChatRoomMessage
import com.example.dynamicmessenger.network.authorization.models.MessageStatus
import com.example.dynamicmessenger.network.chatRooms.SocketManager
import com.example.dynamicmessenger.router.Router
import com.example.dynamicmessenger.userChatRoom.adapters.ChatRoomAdapter
import com.example.dynamicmessenger.userChatRoom.viewModels.ChatRoomViewModel
import com.example.dynamicmessenger.utils.Utils
import com.example.dynamicmessenger.utils.toDate


class ChatRoomFragment : Fragment() {
    private lateinit var viewModel: ChatRoomViewModel
    private lateinit var binding: FragmentChatRoomBinding
    private lateinit var adapter: ChatRoomAdapter
    private var scrollUpWhenKeyboardOpened = true
    private var isAllMessagesDownloaded = false
    private var downloadOldMessages = true
    private var soundPool: SoundPool? = null

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
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        binding.chatRecyclerView.adapter = adapter
        binding.chatRecyclerView.layoutManager = linearLayoutManager
        binding.root.setHasTransientState(true)
//        binding.chatRecyclerView.

        //Toolbar
        setHasOptionsMenu(true)
        configureTopNavBar(binding.chatRoomToolbar)
        observers(receiverID, linearLayoutManager)
        updateRecyclerView(receiverID)
        configureSoundPool()

        //socket
        SocketManager.addChatRoomFragment(this)
        val sendMessageMusic = soundPool!!.load(requireContext(), R.raw.message_sent, 1)

        binding.sendMessageButton.setOnClickListener {
            if (viewModel.userEnteredMessage.value?.isEmpty() == false) {
                SocketManager.sendMessage(receiverID, viewModel.userEnteredMessage)
                soundPool!!.play(sendMessageMusic, 1F, 1F, 0, 0, 1F)
            }
        }

        return binding.root
    }

    //for show toolbar menu
    override fun onPrepareOptionsMenu(menu: Menu) {
        val inflater: MenuInflater = requireActivity().menuInflater
        inflater.inflate(R.menu.chat_top_bar, menu)
        super.onPrepareOptionsMenu(menu)
    }

    override fun onResume() {
        super.onResume()
        SharedConfigs.currentFragment.value = MyFragments.CHAT_ROOM
    }

    override fun onDestroyView() {
        super.onDestroyView()
        SocketManager.removeChatRoomFragment()
        soundPool?.release()
        soundPool = null
//        HomeActivity.isAddContacts = false
    }

    private fun observers(receiverID: String, linearLayoutManager: LinearLayoutManager) {
        SharedConfigs.userRepository.getUserInformation(receiverID).observe(viewLifecycleOwner, Observer {user ->
            if (user != null) {
                HomeActivity.opponentUser = user
                viewModel.toolbarOpponentUsername.value = user.username ?: ""
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
                if ((linearLayoutManager.findFirstVisibleItemPosition() == 0) && (downloadOldMessages)) {
                    downloadOldMessages = false
                    getOldMessages(receiverID)
                }
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

        viewModel.userEnteredMessage.observe(viewLifecycleOwner, Observer {
            SocketManager.messageTyping(receiverID)
        })

        viewModel.opponentTypingTextVisibility.observe(viewLifecycleOwner, Observer {
            if (scrollUpWhenKeyboardOpened) {
                scrollToBottom()
            }
        })
    }

    private fun updateRecyclerView(receiverID: String) {
        viewModel.getMessagesFromNetwork(requireContext(), receiverID) { list, statuses ->
            if (statuses[0].userId == SharedConfigs.signedUser?._id) {
                adapter.myStatuses = statuses[0]
                adapter.opponentStatuses.postValue(statuses[1])
            } else {
                adapter.myStatuses = statuses[1]
                adapter.opponentStatuses.postValue(statuses[0])
            }
            list?.let { roomMessages ->
                adapter.submitList(roomMessages)
                val lastElementNumber = roomMessages.size - 1
                if (roomMessages[lastElementNumber].senderId != SharedConfigs.signedUser?._id) {
                    adapter.myStatuses.readMessageDate.toDate()?.let { date ->
                        if (date < roomMessages[lastElementNumber].createdAt.toDate()) {
                            roomMessages[lastElementNumber].senderId?.let { SocketManager.messageRead(it, roomMessages[lastElementNumber]._id) }
                        }
                    }
                }
            }
            scrollToBottom()
        }
    }

    private fun getOldMessages(receiverID: String) {
        if (!isAllMessagesDownloaded) {
            viewModel.chatRoomProgressBar.postValue(true)
            val lastMessageDate = adapter.data[0].createdAt
            viewModel.getMessagesFromNetwork(requireContext(), receiverID, lastMessageDate) { list, statuses ->
                Log.i("+++", "isAllMessagesDownloaded $isAllMessagesDownloaded")
                if (statuses[0].userId == SharedConfigs.signedUser?._id) {
                    adapter.myStatuses = statuses[0]
                    adapter.opponentStatuses.postValue(statuses[1])
                } else {
                    adapter.myStatuses = statuses[1]
                    adapter.opponentStatuses.postValue(statuses[0])
                }
                if (list.isNullOrEmpty()) {
                    isAllMessagesDownloaded = true
                } else {
                    adapter.configureListWithOldMessages(list.toMutableList())
                }
                downloadOldMessages = true
                viewModel.chatRoomProgressBar.postValue(false)
            }
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
            Router.navigateToFragment(requireActivity(), OpponentInformationFragment())
            return@setOnMenuItemClickListener true
        }
    }

    fun receiveMessage(newMessage: ChatRoomMessage) {
        requireActivity().runOnUiThread {
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

    private val countDownTimer = object: CountDownTimer(2000, 1000) {
        override fun onTick(millisUntilFinished: Long) {}

        override fun onFinish() {
            viewModel.opponentTypingTextVisibility.postValue(false)
        }
    }

    private fun configureSoundPool() {
        val audioAttributes: AudioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        soundPool = SoundPool.Builder()
            .setMaxStreams(6)
            .setAudioAttributes(audioAttributes)
            .build()
    }

    fun opponentTyping(array: Array<Any>) {
        if (HomeActivity.receiverID!! == array[0]) {
            viewModel.opponentTypingTextVisibility.postValue(true)
            countDownTimer.cancel()
            countDownTimer.start()
        }
    }

    fun statusMessageReceived(array: Array<Any>) {
        if (array[1] == HomeActivity.receiverID!!) {
            adapter.opponentStatuses.postValue(adapter.opponentStatuses.value?.apply {
                receivedMessageDate = array[0] as String
            })
        }
    }

    fun statusMessageRead(array: Array<Any>) {
        if (array[1] == HomeActivity.receiverID!!) {
            adapter.opponentStatuses.postValue(adapter.opponentStatuses.value?.apply {
                readMessageDate = array[0] as String
            })
        }
    }
}