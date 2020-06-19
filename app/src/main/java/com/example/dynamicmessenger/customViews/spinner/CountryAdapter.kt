package com.example.dynamicmessenger.customViews.spinner

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView


class CountryAdapter(context: Context?, countryList: List<CountryItem>) :
    ArrayAdapter<CountryItem?>(context!!, 0, countryList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return initView(position, convertView, parent)
    }

    override fun getDropDownView(
        position: Int,
        convertView: View?,
        parent: ViewGroup
    ): View {
        return initView(position, convertView, parent)
    }

    private fun initView(position: Int, convertView: View?, parent: ViewGroup): View {
        val convertView = convertView
            ?: LayoutInflater.from(context).inflate(
                com.example.dynamicmessenger.R.layout.country_spinner_row, parent, false
            )
        val imageViewFlag: ImageView = convertView.findViewById(com.example.dynamicmessenger.R.id.imageViewFlag)
        val textViewName: TextView = convertView.findViewById(com.example.dynamicmessenger.R.id.textViewName)
        val currentItem = getItem(position)
        if (currentItem != null) {
            imageViewFlag.setImageResource(currentItem.flagImage)
            textViewName.text = currentItem.countryName
        }
        return convertView
    }
}