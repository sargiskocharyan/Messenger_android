package com.example.dynamicmessenger.userChatRoom.fragments

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.dynamicmessenger.R
import com.example.dynamicmessenger.activitys.HomeActivity
import com.example.dynamicmessenger.databinding.FragmentOpponentInformationBinding
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

        //Toolbar
        setHasOptionsMenu(true)
        val toolbar: Toolbar = binding.opponentInformationToolbar
        configureTopNavBar(toolbar)

        viewModel.getAvatar {
            binding.opponentProfileAvatarImageView.setImageBitmap(it)
        }

        binding.addToContactsTextView.setOnClickListener {
            viewModel.addUserToContacts {
                if (it) {
                    binding.addToContactsTextView.visibility = View.INVISIBLE
                }
            }
        }

        binding.sendMessageImageView.setOnClickListener {
            HomeActivity.isAddContacts = null
            (context as AppCompatActivity?)!!.supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragmentContainer, ChatRoomFragment())
                .addToBackStack(null)
                .commit()
        }

        configurePage()


        return binding.root
    }

    private fun configureTopNavBar(toolbar: Toolbar) {
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        toolbar.elevation = 10.0F
        toolbar.title = ""
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24)
//        toolbar.inflateMenu(R.menu.chat_top_bar)
        toolbar.background = ColorDrawable(ContextCompat.getColor(requireContext(), R.color.white))
        toolbar.setNavigationOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    private fun configurePage() {
        val opponentUser = HomeActivity.opponentUser
        //TODO:binding
        binding.opponentInformationToolbarTextView.text = opponentUser?.username
        binding.opponentInfo.text = opponentUser?.info
        binding.opponentInfoUsernameTextView.text = opponentUser?.username
        binding.opponentInfoNameTextView.text = opponentUser?.name
        binding.opponentInfoLastNameTextView.text = opponentUser?.lastname
        binding.opponentInfoPhoneTextView.text = opponentUser?.phoneNumber
        binding.opponentInfoEmailTextView.text = opponentUser?.email
        binding.opponentInfoGenderTextView.text = opponentUser?.gender
        binding.opponentInfoBirthDateTextView.text = opponentUser?.birthday?.substring(0, 10)
        binding.opponentInfoAddressTextView.text = opponentUser?.address

        if (HomeActivity.isAddContacts == false) {
            binding.addToContactsTextView.visibility = View.INVISIBLE
        } else if (HomeActivity.isAddContacts == null) {
            binding.addToContactsTextView.visibility = View.INVISIBLE
            binding.sendMessageImageView.visibility = View.INVISIBLE
        }
    }
}