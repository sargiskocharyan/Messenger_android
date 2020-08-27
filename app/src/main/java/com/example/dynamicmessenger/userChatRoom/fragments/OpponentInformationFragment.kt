package com.example.dynamicmessenger.userChatRoom.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.dynamicmessenger.R
import com.example.dynamicmessenger.activitys.HomeActivity
import com.example.dynamicmessenger.common.MyFragments
import com.example.dynamicmessenger.common.SharedConfigs
import com.example.dynamicmessenger.databinding.FragmentOpponentInformationBinding
import com.example.dynamicmessenger.userCalls.CallRoomActivity
import com.example.dynamicmessenger.userChatRoom.viewModels.OpponentInformationViewModel

class OpponentInformationFragment : Fragment() {

    private lateinit var viewModel: OpponentInformationViewModel
    private lateinit var binding: FragmentOpponentInformationBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(OpponentInformationViewModel::class.java)
        binding = FragmentOpponentInformationBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        SharedConfigs.currentFragment.value = MyFragments.OPPONENT_INFORMATION

        //Toolbar
        setHasOptionsMenu(true)
        onClickListeners()
        configurePage()
        observers()

        return binding.root
    }

    private fun configureTopNavBar(toolbar: Toolbar) {
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    private fun configurePage() {
        configureTopNavBar(binding.opponentInformationToolbar)
        val opponentUser = HomeActivity.opponentUser
        //TODO:binding
        binding.opponentInfoBirthDateTextView.text = opponentUser?.birthday?.substring(0, 10)

        if (HomeActivity.isAddContacts == false) {
//            binding.addToContactsImageView.visibility = View.GONE
        } else if (HomeActivity.isAddContacts == null) {
//            binding.addToContactsImageView.visibility = View.GONE
            binding.sendMessageImageView.visibility = View.GONE
        }
    }

    private fun onClickListeners() {
        binding.callOpponentImageView.setOnClickListener {
            val opponentUser = HomeActivity.opponentUser!!
            SharedConfigs.callingOpponentId = opponentUser._id
            val intent = Intent(activity, CallRoomActivity::class.java)
            startActivity(intent)
        }

        binding.sendMessageImageView.setOnClickListener {
            HomeActivity.isAddContacts = null
            requireActivity().supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragmentContainer, ChatRoomFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    private fun observers() {
        SharedConfigs.userRepository.getUserInformation(HomeActivity.receiverID).observe(viewLifecycleOwner, Observer {user ->
            viewModel.getOpponentInformation(user)
            HomeActivity.opponentUser = user
            SharedConfigs.userRepository.getAvatar(user?.avatarURL).observe(viewLifecycleOwner, Observer {
                binding.opponentProfileAvatarImageView.setImageBitmap(it)
            })
        })
    }
}