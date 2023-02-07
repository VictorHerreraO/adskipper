package com.alfeugds.adskipper.delegate

import android.content.Context
import androidx.core.content.edit
import androidx.preference.PreferenceManager

object PreferenceKey {
    const val SETTINGS_ENABLE_SERVICE = "enable-service"
    const val SETTINGS_MUTE_AUDIO = "mute-audio"
}

class PreferenceManagerDelegate(
    context: Context
) {
    private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
        context.applicationContext
    )

    var isServiceEnabled: Boolean
        get() = sharedPreferences.getBoolean(PreferenceKey.SETTINGS_ENABLE_SERVICE, true)
        set(value) = sharedPreferences.edit {
            putBoolean(PreferenceKey.SETTINGS_ENABLE_SERVICE, value)
        }

    var isMuteAdEnabled: Boolean
        get() = sharedPreferences.getBoolean(PreferenceKey.SETTINGS_MUTE_AUDIO, true)
        set(value) = sharedPreferences.edit {
            putBoolean(PreferenceKey.SETTINGS_MUTE_AUDIO, value)
        }
}