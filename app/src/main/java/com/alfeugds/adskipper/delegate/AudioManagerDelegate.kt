package com.alfeugds.adskipper.delegate

import android.app.Service
import android.content.Context
import android.media.AudioManager
import android.os.Build
import android.util.Log

private const val TAG = "AudioManagerDelegate"

class AudioManagerDelegate(
    service: Service,
    private val isMuteAdEnabled: () -> Boolean
) {
    private val audioManager = service.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    private var isMuted = false


    fun muteMedia() {
        if (isMuted || !isMuteAdEnabled()) return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            audioManager.adjustStreamVolume(
                AudioManager.STREAM_MUSIC,
                AudioManager.ADJUST_MUTE,
                0
            )
        } else {
            @Suppress("DEPRECATION")
            audioManager.setStreamMute(AudioManager.STREAM_MUSIC, true)
        }

        Log.i(TAG, "STREAM_MUSIC muted.")
        isMuted = true
    }

    fun unMuteMedia() {
        if (!isMuted || !isMuteAdEnabled()) return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            audioManager.adjustStreamVolume(
                AudioManager.STREAM_MUSIC,
                AudioManager.ADJUST_UNMUTE,
                0
            )
        } else {
            @Suppress("DEPRECATION")
            audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false)
        }

        Log.i(TAG, "STREAM_MUSIC unmuted.")
        isMuted = false
    }
}