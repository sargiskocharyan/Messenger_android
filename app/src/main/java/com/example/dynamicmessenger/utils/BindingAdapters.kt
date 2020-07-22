package com.example.dynamicmessenger.utils

import android.graphics.Color
import android.view.View
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.databinding.BindingAdapter
import com.example.dynamicmessenger.R
import de.hdodenhof.circleimageview.CircleImageView

@BindingAdapter("languageIMG")
fun setLanguageIG(image: ImageView, resource: Int) {
    image.setImageResource(resource)
}

@BindingAdapter("setTextViewVisible")
fun setTextViewVisible(textView: TextView, answer: Boolean) {
    if (answer) {
        textView.visibility = View.VISIBLE
    } else {
        textView.visibility = View.INVISIBLE
    }
}

@BindingAdapter("setHintColour")
fun setHintColour(textView: TextView, answer: Boolean) {
    if (answer) {
        textView.setTextColor(Color.BLUE)
    } else {
        textView.setTextColor(Color.RED)
    }
}

@BindingAdapter("setContinueButtonConfiguration")
fun setContinueButtonConfiguration(button: Button, answer: Boolean) {
    button.isEnabled = answer
    if (answer) {
        button.setBackgroundResource(R.drawable.enable_button_design)
    } else {
        button.setBackgroundResource(R.drawable.disable_button_design)
    }
}

@BindingAdapter("setEditTextBackground")
fun setEditTextBackground(editText: EditText, answer: Boolean) {
    if (answer) {
        editText.setBackgroundResource(R.drawable.edittext_blue_underline)
    } else {
        editText.setBackgroundResource(R.drawable.edittext_red_underline)
    }
}

@BindingAdapter("setProgressBarVisible")
fun setProgressBarVisible(progressBar: ProgressBar, answer: Boolean) {
    if (answer) {
        progressBar.visibility = View.VISIBLE
    } else {
        progressBar.visibility = View.INVISIBLE
    }
}

@BindingAdapter("loginRegisterTextChange")
fun loginRegisterTextChange(textView: TextView, answer: Boolean) {
    if (answer) {
        textView.setText(R.string.login)
    } else {
        textView.setText(R.string.register)
    }
}

@BindingAdapter("setCircleImageVisible")
fun setCircleImageVisible(circleImage: CircleImageView, answer: Boolean) {
    if (answer) {
        circleImage.visibility = View.VISIBLE
    } else {
        circleImage.visibility = View.INVISIBLE
    }
}

@BindingAdapter("setCardViewVisible")
fun setCardViewVisible(cardView: CardView, answer: Boolean) {
    if (answer) {
        cardView.visibility = View.VISIBLE
    } else {
        cardView.visibility = View.GONE
    }
}