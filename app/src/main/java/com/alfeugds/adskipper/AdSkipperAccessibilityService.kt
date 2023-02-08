package com.alfeugds.adskipper

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.os.Build
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.alfeugds.adskipper.binding.AccessibilityBinding
import com.alfeugds.adskipper.delegate.AudioManagerDelegate
import com.alfeugds.adskipper.delegate.PreferenceManagerDelegate

class AdSkipperAccessibilityService : AccessibilityService() {

    private var _binding: AccessibilityBinding? = null
    private val binding: AccessibilityBinding get() = _binding!!

    private lateinit var audioManager: AudioManagerDelegate
    private lateinit var prefsManager: PreferenceManagerDelegate

    override fun onCreate() {
        super.onCreate()
        logMessage("onCreate fired")
        prefsManager = PreferenceManagerDelegate(this)
        audioManager = AudioManagerDelegate(this) {
            prefsManager.isMuteAdEnabled
        }
        _binding = AccessibilityBinding(this)
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        logMessage("onTaskRemoved fired")
        disable()
        super.onTaskRemoved(rootIntent)
    }

    override fun onDestroy() {
        logMessage("onDestroy fired")
        _binding = null
        super.onDestroy()
    }

    override fun onInterrupt() {
        logMessage("onInterrupt fired")
    }

    private fun disable() {
        logMessage("Disabling service with stopSelf")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopSelf()
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (!prefsManager.isServiceEnabled) {
            logMessage("Service is not supposed to be enabled.")
            return
        }
        try {
            if (binding.hasNoBindings) {
                audioManager.unMuteMedia()
                logMessage("No ads yet...")
                return
            }

            logMessage("player_learn_more_button or skipAdButton or adProgressText are visible. Trying to skip ad...")

            audioManager.muteMedia()

            binding.skipAdButton
                ?.takeIf { it.isClickable }
                ?.run {
                    logMessage("skipAdButton is clickable! Trying to click it...")
                    performAction(AccessibilityNodeInfo.ACTION_CLICK)
                    logMessage("Clicked skipAdButton!")
                }
                ?: logMessage("skipAdButton is null... returning...")
        } catch (error: Exception) {
            logError(error, "Something went wrong...")
        }
    }
}