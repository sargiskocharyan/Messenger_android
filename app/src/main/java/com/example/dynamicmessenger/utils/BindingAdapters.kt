package com.example.dynamicmessenger.utils

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.*
import androidx.databinding.BindingAdapter
import com.airbnb.lottie.LottieAnimationView
import com.example.dynamicmessenger.R
import com.example.dynamicmessenger.common.AppLangKeys
import com.google.android.material.textfield.TextInputLayout
import de.hdodenhof.circleimageview.CircleImageView


//@BindingAdapter("languageIMG")
//fun setLanguageIG(image: ImageView, resource: Int) {
//    image.setImageResource(resource)
//}
@BindingAdapter("languageIMG")
fun setLanguageIG(image: ImageView, resource: AppLangKeys) {
    when (resource) {
        AppLangKeys.EN -> image.setImageResource(R.drawable.ic_united_kingdom)
        AppLangKeys.RU -> image.setImageResource(R.drawable.ic_russia)
        else -> image.setImageResource(R.drawable.ic_armenia)
    }
}

@BindingAdapter("setTextViewVisible")
fun setTextViewVisible(textView: TextView, answer: Boolean) {
    if (answer) {
        textView.visibility = View.VISIBLE
    } else {
        textView.visibility = View.INVISIBLE
    }
}

@BindingAdapter("setTextViewGone")
fun setTextViewGone(textView: TextView, answer: Boolean) {
    if (answer) {
        textView.visibility = View.VISIBLE
    } else {
        textView.visibility = View.GONE
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

@BindingAdapter("setViewVisible")
fun setViewVisible(view: View, answer: Boolean) {
    if (answer) {
        view.visibility = View.VISIBLE
    } else {
        view.visibility = View.INVISIBLE
    }
}

@BindingAdapter("setCardViewVisible")
fun setCardViewVisible(cardView: View, answer: Boolean) {
    if (answer) {
        cardView.visibility = View.VISIBLE
    } else {
        cardView.visibility = View.GONE
    }
}

@BindingAdapter("setCircleImageBitmap")
fun setCircleImageBitmap(circleImage: CircleImageView, bitmap: Bitmap?) {
    if (bitmap == null) {
        circleImage.setImageResource(R.drawable.ic_user_image)
    } else {
        circleImage.setImageBitmap(bitmap)
    }
}

@BindingAdapter("setImageViewBitmap")
fun setImageViewBitmap(image: ImageView, bitmap: Bitmap?) {
    if (bitmap == null) {
        image.setImageResource(R.drawable.ic_user_image)
    }
    image.setImageBitmap(bitmap)
}

@BindingAdapter("setMicrophoneImage")
fun setMicrophoneImage(circleImage: ImageView, answer: Boolean) {
    if (answer) {
        circleImage.setImageResource(R.drawable.ic_baseline_mic_on_24)
    } else {
        circleImage.setImageResource(R.drawable.ic_baseline_mic_off_24)
    }
}

@BindingAdapter("setVolumeImage")
fun setVolumeImage(circleImage: ImageView, answer: Boolean) {
    if (answer) {
        circleImage.setImageResource(R.drawable.ic_baseline_volume_up_24)
    } else {
        circleImage.setImageResource(R.drawable.ic_baseline_volume_off_24)
    }
}

@BindingAdapter("setAddContactImage")
fun setAddContactImage(image: ImageView, answer: Boolean) {
    if (!answer) {
        image.setImageResource(R.drawable.ic_baseline_person_add_24)
    } else {
        image.setImageResource(R.drawable.ic_baseline_person_add_disabled_24)
    }
}

@BindingAdapter("setImageVisibility")
fun setImageVisibility(image: ImageView, answer: Boolean) {
    if (answer) {
        image.visibility = View.VISIBLE
    } else {
        image.visibility = View.GONE
    }
}

@BindingAdapter("setEmailErrorText")
fun setEmailErrorText(textInputLayout: TextInputLayout, answer: Boolean) {
    if (answer) {
        textInputLayout.error = null
    } else {
        textInputLayout.error = textInputLayout.context.getString(R.string.incorrect_email)
    }
}

@BindingAdapter("setCodeErrorText")
fun setCodeErrorText(textInputLayout: TextInputLayout, answer: Boolean) {
    if (answer) {
        textInputLayout.error = null
    } else {
        textInputLayout.error = textInputLayout.context.getString(R.string.incorrect_code)
    }
}

@BindingAdapter("setUsernameErrorText")
fun setUsernameErrorText(textInputLayout: TextInputLayout, answer: Boolean?) {
    when (answer) {
        true -> textInputLayout.error = null
        false -> textInputLayout.error =
            textInputLayout.context.getString(R.string.the_username_must_contain_at_least_4_letters)
        null -> textInputLayout.error =
            textInputLayout.context.getString(R.string.this_username_is_taken)
    }
}

@BindingAdapter("setNameErrorText")
fun setNameErrorText(textInputLayout: TextInputLayout, answer: Boolean) {
    if (answer) {
        textInputLayout.error = null
    } else {
        textInputLayout.error = textInputLayout.context.getString(R.string.name_must_contain_at_least_2_letters)
    }
}

@BindingAdapter("setLastNameErrorText")
fun setLastNameErrorText(textInputLayout: TextInputLayout, answer: Boolean) {
    if (answer) {
        textInputLayout.error = null
    } else {
        textInputLayout.error = textInputLayout.context.getString(R.string.lastname_must_contain_at_least_2_letters)
    }
}

@BindingAdapter("setLottieAnimationGone")
fun setLottieAnimationGone(animation: LottieAnimationView, answer: Boolean) {
    if (answer) {
        animation.visibility = View.VISIBLE
    } else {
        animation.visibility = View.GONE
    }
}