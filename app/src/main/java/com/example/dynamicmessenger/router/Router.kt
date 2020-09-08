package com.example.dynamicmessenger.router

import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.navigation.findNavController
import com.example.dynamicmessenger.R

object Router {
    fun navigateToFragment(activity: FragmentActivity, fragment: Fragment) {
        activity.supportFragmentManager
        .beginTransaction()
        .replace(R.id.fragmentContainer, fragment)
        .addToBackStack(null)
        .commit()
    }

    fun navControllerNavigate(view: View) {
        view.findNavController().navigate(R.id.action_emailAndPhoneFragment_to_personLoginFragment)
    }

}
