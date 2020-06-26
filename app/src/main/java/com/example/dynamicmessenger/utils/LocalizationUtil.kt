package com.example.dynamicmessenger.utils

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.util.DisplayMetrics
import java.util.*


object LocalizationUtil {
    fun applyLanguage(context: Context, language: String): Context {
        val locale = Locale(language)
        val configuration = context.resources.configuration
        val displayMetrics = context.resources.displayMetrics

        Locale.setDefault(locale)

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLocale(locale)
            context.createConfigurationContext(configuration)
        } else {
            configuration.locale = locale
            context.resources.updateConfiguration(configuration, displayMetrics)
            context
        }
    }

    fun setApplicationLocale(context: Context,locale: String) {
        val resources: Resources = context.resources
        val dm: DisplayMetrics = resources.displayMetrics
        val config: Configuration = resources.configuration
        config.setLocale(Locale(locale))
        resources.updateConfiguration(config, dm)
    }
    fun updateResources(context: Context, language: String): Context? {
        var context = context
        val locale = Locale(language)
        Locale.setDefault(locale)
        val res = context.resources
        val config = Configuration(res.configuration)
        config.setLocale(locale)
        context = context.createConfigurationContext(config)
        return context
    }

}