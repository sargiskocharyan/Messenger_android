package com.example.dynamicmessenger.utils

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.example.dynamicmessenger.R
import com.example.dynamicmessenger.common.AppLangKeys
import com.example.dynamicmessenger.common.SharedConfigs

//class BindingAdapters {
    @BindingAdapter("languageIMG")
    fun setLanguageIG(image: ImageView, resource: Int) {
        image.setImageResource(resource)
    }
//}
