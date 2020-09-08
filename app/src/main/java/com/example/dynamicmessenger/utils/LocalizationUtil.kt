package com.example.dynamicmessenger.utils

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.util.DisplayMetrics
import java.util.*


object LocalizationUtil {
    fun setApplicationLocale(context: Context, locale: String) {
        val resources: Resources = context.resources
        val dm: DisplayMetrics = resources.displayMetrics
        val config: Configuration = resources.configuration

        val uiMode = config.uiMode
        config.setTo(context.resources.configuration)
        config.uiMode = uiMode

        config.setLocale(Locale(locale))
        resources.updateConfiguration(config, dm)
    }

    fun updateResources(context: Context, language: String): Context? {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val res = context.resources
        val config = Configuration(res.configuration)
        config.setLocale(locale)
        return context.createConfigurationContext(config)
    }

}