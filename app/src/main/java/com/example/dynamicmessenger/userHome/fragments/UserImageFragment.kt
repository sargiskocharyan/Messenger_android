package com.example.dynamicmessenger.userHome.fragments

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.dynamicmessenger.common.SharedConfigs
import com.example.dynamicmessenger.databinding.FragmentChatRoomBinding
import com.example.dynamicmessenger.databinding.FragmentUserImageBinding
import com.example.dynamicmessenger.network.authorization.LoadAvatarApi
import com.example.dynamicmessenger.userDataController.database.DiskCache
import com.jakewharton.disklrucache.DiskLruCache
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class UserImageFragment : Fragment() {
    private lateinit var binding: FragmentUserImageBinding
    private var activityJob = Job()
    private val coroutineScope = CoroutineScope(activityJob + Dispatchers.Main)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val diskLruCache = DiskCache.getInstance(requireContext())
        binding = FragmentUserImageBinding.inflate(layoutInflater)
        getAvatar(diskLruCache) {
            binding.bigAvatarImageView.setImageBitmap(it)
        }

        binding.bigAvatarBackImageView.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        return binding.root
    }

    private fun getAvatar(diskLruCache: DiskCache, closure: (Bitmap) -> Unit) {
        coroutineScope.launch {
            if (SharedConfigs.signedUser?.avatarURL != null) {
                try {
                    if (diskLruCache.get(SharedConfigs.signedUser?.avatarURL!!) != null) {
                        closure(diskLruCache.get(SharedConfigs.signedUser?.avatarURL!!)!!)
                    } else {
                        val response = LoadAvatarApi.retrofitService.loadAvatarResponseAsync(
                            SharedConfigs.signedUser!!.avatarURL!!)
                        if (response.isSuccessful) {
                            val inputStream = response.body()!!.byteStream()
                            val bitmap = BitmapFactory.decodeStream(inputStream)
                            diskLruCache.put(SharedConfigs.signedUser?.avatarURL!!, bitmap)
                            closure(bitmap)
                        }
                    }
                } catch (e: Exception) {
                    Log.i("+++exception", "userInformationViewModel getAvatar $e")
                }
            }
        }
    }


}